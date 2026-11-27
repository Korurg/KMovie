package com.github.korurg.jackett

import com.github.korurg.configuration.data.RemoteJackettInstance
import com.github.korurg.jackett.domain.SearchResult
import com.github.korurg.jackett.domain.SearchResults
import com.github.korurg.jackett.domain.VideoQuality
import com.github.korurg.jackett.domain.native.NativeInfo
import com.github.korurg.jackett.domain.native.NativeProbeStream
import com.github.korurg.jackett.domain.native.NativeRelease
import com.github.korurg.jackett.domain.native.NativeSearchResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.json.Json

class JacketNativeClient(private val client: HttpClient, private val json: Json) {
    suspend fun search(instance: RemoteJackettInstance, query: String): SearchResults {
        val response = client.get("${instance.url.trimEnd('/')}/api/v2.0/indexers/all/results") {
            parameter("apikey", instance.apiKey)
            parameter("query", query)
        }
        val root = json.decodeFromString<NativeSearchResponse>(response.bodyAsText())
        val failures = root.indexers.orEmpty().mapNotNull { indexer ->
            val status = indexer.status?.takeIf { it.isNotBlank() }
            if (!indexer.error.isNullOrBlank() || (status != null && status != "2" && !status.equals("ok", true))) {
                indexer.name.orEmpty()
            } else null
        }
        return SearchResults(root.results.map { it.toResult() }, failures)
    }
}

private fun NativeRelease.toResult(): SearchResult {
    val videoStreams = ffprobe.orEmpty()
        .filter { it.codecType.equals("video", true) && !it.codecName.equals("mjpeg", true) }
    val durations = videoStreams.mapNotNull { it.tags?.duration?.let(::parseDurationSeconds) }
    val avg = durations.takeIf { it.isNotEmpty() }?.average()?.toLong()
    return SearchResult(
        title = title,
        size = size?.takeIf { it > 0 },
        quality = qualityFromText(title)
            ?: qualityFromProbe(videoStreams)
            ?: qualityFromInfo(info)
            ?: VideoQuality.UNKNOWN,
        categories = category.orEmpty().map { it.toString() }.ifEmpty { listOfNotNull(categoryDesc) },
        audio = info?.voices.orEmpty().filter { it.isNotBlank() }
            .ifEmpty { languages.orEmpty().filter { it.isNotBlank() } },
        seasons = info?.seasons.orEmpty().filter { it > 0 }.distinct().sorted(),
        seeders = seeders?.takeIf { it >= 0 }?.toLong(),
        peers = peers?.takeIf { it >= 0 }?.toLong(),
        episodeCount = durations.size,
        avgEpisodeSeconds = avg,
        totalSeconds = durations.sum().takeIf { it > 0 }
    )
}

private fun qualityFromText(text: String?): VideoQuality? {
    if (text == null) return null
    return when {
        Regex("""(?i)(?<![a-z0-9])(2160p|4k)(?![a-z0-9])""").containsMatchIn(text) -> VideoQuality.P2160
        Regex("""(?i)(?<![a-z0-9])1080p(?![a-z0-9])""").containsMatchIn(text) -> VideoQuality.P1080
        Regex("""(?i)(?<![a-z0-9])720p(?![a-z0-9])""").containsMatchIn(text) -> VideoQuality.P720
        else -> null
    }
}

private fun qualityFromProbe(streams: List<NativeProbeStream>): VideoQuality? =
    streams.mapNotNull { stream ->
        val width = stream.width ?: return@mapNotNull null
        val height = stream.height ?: return@mapNotNull null
        when {
            width >= 3840 || height >= 2160 -> VideoQuality.P2160
            width >= 1920 || height >= 1080 -> VideoQuality.P1080
            width >= 1280 || height >= 720 -> VideoQuality.P720
            else -> null
        }
    }.maxByOrNull { it.ordinal }

private fun qualityFromInfo(info: NativeInfo?): VideoQuality? = when (val quality = info?.quality) {
    null -> null
    in 2160..Int.MAX_VALUE -> VideoQuality.P2160
    in 1080 until 2160 -> VideoQuality.P1080
    in 720 until 1080 -> VideoQuality.P720
    else -> null
}

/** "02:52:35.721000000" -> seconds */
private fun parseDurationSeconds(raw: String): Long? {
    val parts = raw.substringBefore('.').split(':')
    if (parts.size != 3) return null
    val hours = parts[0].trim().toLongOrNull() ?: return null
    val minutes = parts[1].toLongOrNull() ?: return null
    val seconds = parts[2].toLongOrNull() ?: return null
    return hours * 3600 + minutes * 60 + seconds
}
