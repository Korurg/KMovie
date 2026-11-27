package com.github.korurg.jackett

import com.github.korurg.configuration.ConfigurationService
import com.github.korurg.configuration.data.RemoteJackettInstance
import com.github.korurg.jackett.domain.SearchResult
import com.github.korurg.jackett.domain.SearchResults
import com.github.korurg.jackett.domain.VideoQuality
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.util.LinkedHashMap

enum class SortOption(val defaultDescending: Boolean) {
    PEERS(true), SIZE(true), QUALITY(true)
}

data class SearchFilters(
    val category: String? = null,
    val voice: String? = null,
    val quality: VideoQuality? = null,
    val season: Int? = null
)

data class FilterOption<T>(val value: T, val available: Boolean)

data class SearchResponse(
    val items: List<SearchResult> = emptyList(),
    val categories: List<FilterOption<String>> = emptyList(),
    val voices: List<FilterOption<String>> = emptyList(),
    val qualities: List<FilterOption<VideoQuality>> = VideoQuality.entries
        .filter { it != VideoQuality.UNKNOWN }.map { FilterOption(it, false) },
    val seasons: List<FilterOption<Int>> = emptyList(),
    val failedIndexers: List<String> = emptyList(),
    val totalResults: Int = 0
)

class JackettNotConfiguredException : IllegalStateException()

class SearchService(
    private val instanceProvider: () -> RemoteJackettInstance?,
    private val fetch: suspend (RemoteJackettInstance, String) -> SearchResults
) {
    constructor(configuration: ConfigurationService, client: JacketNativeClient) : this(
        { configuration.appConfig.jacketConfig.remoteJackettInstance },
        { instance, query -> client.search(instance, query) }
    )

    private data class CacheKey(val instance: RemoteJackettInstance, val query: String)

    private val lock = Any()
    private val cache = object : LinkedHashMap<CacheKey, SearchResults>(16, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<CacheKey, SearchResults>?) = size > 10
    }
    private val pending = mutableMapOf<CacheKey, CompletableDeferred<SearchResults>>()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun search(
        query: String,
        filters: SearchFilters = SearchFilters(),
        sort: SortOption = SortOption.PEERS,
        descending: Boolean = sort.defaultDescending,
        refresh: Boolean = false
    ): SearchResponse {
        val instance = instanceProvider()
            ?.takeIf { it.url.isNotBlank() && it.apiKey.isNotBlank() }
            ?: throw JackettNotConfiguredException()
        val key = CacheKey(instance, query.trim())
        val deferred = synchronized(lock) {
            if (refresh) {
                cache.remove(key)
                pending.remove(key)
            }
            cache[key]?.let { return@synchronized CompletableDeferred(it) }
            pending[key] ?: CompletableDeferred<SearchResults>().also { request ->
                pending[key] = request
                scope.launch {
                    try {
                        val response = fetch(instance, key.query)
                        synchronized(lock) {
                            if (pending[key] === request) {
                                pending.remove(key)
                                cache[key] = response
                            }
                        }
                        request.complete(response)
                    } catch (error: Throwable) {
                        synchronized(lock) {
                            if (pending[key] === request) pending.remove(key)
                        }
                        request.completeExceptionally(error)
                    }
                }
            }
        }
        return filter(deferred.await(), filters, sort, descending)
    }
}

private fun categoryGroup(value: String): String {
    val number = value.toIntOrNull() ?: return value
    return if (number / 1000 in 1..8) ((number / 1000) * 1000).toString() else value
}

private fun filter(response: SearchResults, filters: SearchFilters, sort: SortOption, descending: Boolean): SearchResponse {
    val results = response.items
    val byCategory = results.filter { filters.category == null || it.categories.any { value -> categoryGroup(value) == filters.category } }
    val byVoice = byCategory.filter { filters.voice == null || it.audio.any { value -> value.equals(filters.voice, true) } }
    val byQuality = byVoice.filter { filters.quality == null || it.quality == filters.quality }
    val availableVoices = byCategory.flatMap { it.audio }
    val availableQualities = byVoice.map { it.quality }.toSet()
    val availableSeasons = byQuality.flatMap { it.seasons }.toSet()
    val comparator = when (sort) {
        SortOption.PEERS -> compareBy<SearchResult> { it.seeders ?: 0L }
        SortOption.SIZE -> compareBy { it.size ?: 0L }
        SortOption.QUALITY -> compareBy { it.quality.ordinal }
    }
    return SearchResponse(
        items = byQuality.filter { filters.season == null || filters.season in it.seasons }
            .sortedWith(if (descending) comparator.reversed() else comparator),
        categories = results.flatMap { it.categories }.map(::categoryGroup).distinct().sorted()
            .map { FilterOption(it, true) },
        voices = results.flatMapTo(sortedSetOf(String.CASE_INSENSITIVE_ORDER)) { it.audio }
            .filter { it.isNotBlank() }.sorted()
            .map { voice -> FilterOption(voice, availableVoices.any { it.equals(voice, true) }) },
        qualities = VideoQuality.entries.filter { it != VideoQuality.UNKNOWN }
            .map { FilterOption(it, it in availableQualities) },
        seasons = results.flatMap { it.seasons }.filter { it > 0 }.distinct().sorted()
            .map { FilterOption(it, it in availableSeasons) },
        failedIndexers = response.failedIndexers,
        totalResults = results.size
    )
}
