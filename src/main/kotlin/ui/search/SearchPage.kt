package com.github.korurg.ui.search

import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.github.korurg.jackett.*
import com.github.korurg.jackett.domain.SearchResult
import com.github.korurg.jackett.domain.VideoQuality
import com.github.korurg.ui.localizedText
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import org.koin.compose.koinInject
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SearchPage(
    service: SearchService = koinInject()
) {
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    var quality by remember { mutableStateOf<VideoQuality?>(null) }
    var voice by remember { mutableStateOf<String?>(null) }
    var season by remember { mutableStateOf<Int?>(null) }
    var sort by remember { mutableStateOf(SortOption.PEERS) }
    var descending by remember { mutableStateOf(SortOption.PEERS.defaultDescending) }
    var loading by remember { mutableStateOf(false) }
    var searched by remember { mutableStateOf(false) }
    var response by remember { mutableStateOf(SearchResponse()) }
    var error by remember { mutableStateOf<String?>(null) }
    var debouncedQuery by remember { mutableStateOf("") }
    var refreshVersion by remember { mutableIntStateOf(0) }
    var refreshPending by remember { mutableStateOf(false) }

    LaunchedEffect(query) {
        debouncedQuery = ""
        response = SearchResponse()
        searched = false
        error = null
        category = null
        voice = null
        season = null
        loading = false
        val searchQuery = query.trim()
        if (searchQuery.length < 3) return@LaunchedEffect
        delay(600.milliseconds)
        debouncedQuery = searchQuery
    }

    LaunchedEffect(debouncedQuery, category, voice, quality, season, sort, descending, refreshVersion) {
        if (debouncedQuery.isEmpty()) return@LaunchedEffect
        val refresh = refreshPending
        refreshPending = false
        loading = true
        error = null
        try {
            response = service.search(
                query = debouncedQuery,
                filters = SearchFilters(category, voice, quality, season),
                sort = sort,
                descending = descending,
                refresh = refresh
            )
            searched = true
        } catch (e: CancellationException) {
            throw e
        } catch (_: JackettNotConfiguredException) {
            error = "searchConfigureJackett"
        } catch (_: Exception) {
            error = "searchFailed"
        } finally {
            loading = false
        }
    }

    Column(Modifier.fillMaxSize().padding(24.dp)) {
        Text(localizedText("search"), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            SearchField(
                value = query,
                onValueChange = { query = it },
                onClear = { query = "" },
                placeholder = localizedText("searchPlaceholder"),
                modifier = Modifier.weight(1f)
            )
            IconButton(
                enabled = debouncedQuery.isNotEmpty() && !loading,
                onClick = { refreshPending = true; refreshVersion++ }
            ) {
                Icon(Icons.Default.Refresh, contentDescription = localizedText("refreshSearch"))
            }
        }
        Spacer(Modifier.height(12.dp))
        FilterRow(label = localizedText("filterCategory")) {
            CategoryChips(response.categories.map { it.value }, category) { category = it }
        }
        Spacer(Modifier.height(8.dp))
        FilterRow(label = localizedText("filterAudio")) {
            VoiceChips(
                voices = response.voices.map { it.value },
                available = response.voices.filter { it.available }.map { it.value },
                selected = voice,
                onSelect = { voice = it }
            )
        }
        Spacer(Modifier.height(8.dp))
        FilterRow(label = localizedText("filterQuality")) {
            QualityChips(
                selected = quality,
                available = response.qualities.filter { it.available }.map { it.value }.toSet(),
                onSelect = { quality = it },
                label = { qualityLabel(it) }
            )
        }
        Spacer(Modifier.height(8.dp))
        FilterRow(label = localizedText("filterSeasons")) {
            SeasonChips(
                seasons = response.seasons.map { it.value },
                available = response.seasons.filter { it.available }.map { it.value }.toSet(),
                selected = season,
                onSelect = { season = it }
            )
        }
        Spacer(Modifier.height(8.dp))
        FilterRow(label = localizedText("filterSort")) {
            SortChips(
                selected = sort,
                descending = descending,
                onSortSelect = {
                    if (sort == it) {
                        descending = !descending
                    } else {
                        sort = it
                        descending = it.defaultDescending
                    }
                },
                label = { sortLabel(it) }
            )
        }
        Spacer(Modifier.height(12.dp))
        if (loading) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 3.dp)
                Spacer(Modifier.width(10.dp))
                Text(
                    localizedText("searching"),
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
        error?.let { Text(localizedText(it), color = MaterialTheme.colorScheme.error) }
        if (response.failedIndexers.isNotEmpty()) {
            val unknownIndexer = localizedText("unknownIndexer")
            Text(
                "${localizedText("indexersFailed")}: ${response.failedIndexers.joinToString { it.ifBlank { unknownIndexer } }}",
                color = MaterialTheme.colorScheme.tertiary
            )
        }
        if (searched && response.items.isEmpty()) {
            Text(localizedText(if (response.failedIndexers.isNotEmpty() && response.totalResults == 0) "noResultsIndexerFailure" else "nothingFound"))
        }
        val listState = rememberLazyListState()
        Box(Modifier.fillMaxWidth().weight(1f)) {
            LazyColumn(
                state = listState,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(response.items) { result -> SearchResultItem(result) }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(listState),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight()
            )
        }
    }
}

@Composable
private fun FilterRow(label: String, content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        content()
    }
}

@Composable
private fun CategoryChips(categories: List<String>, selected: String?, onSelect: (String?) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(localizedText("all")) },
            colors = chipColors(selected == null)
        )
        categories.forEach { category ->
            val isSelected = category == selected
            FilterChip(
                selected = isSelected,
                onClick = { onSelect(if (isSelected) null else category) },
                label = { Text(categoryLabel(category)) },
                colors = chipColors(isSelected)
            )
        }
    }
}

@Composable
private fun SearchField(
    value: String,
    onValueChange: (String) -> Unit,
    onClear: () -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (value.isNotEmpty()) {
                IconButton(onClick = onClear) {
                    Icon(Icons.Default.Close, contentDescription = localizedText("clear"))
                }
            }
        },
        modifier = modifier
    )
}

@Composable
private fun QualityChips(
    selected: VideoQuality?,
    available: Set<VideoQuality>,
    onSelect: (VideoQuality?) -> Unit,
    label: @Composable (VideoQuality) -> String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(localizedText("all")) },
            colors = chipColors(selected == null)
        )
        VideoQuality.entries
            .filter { it != VideoQuality.UNKNOWN }
            .forEach { option ->
                val isSel = selected == option
                FilterChip(
                    selected = isSel,
                    enabled = option in available || isSel,
                    onClick = { onSelect(if (isSel) null else option) },
                    label = { Text(label(option)) },
                    colors = chipColors(isSel, isSel && option !in available)
                )
            }
    }
}

@Composable
private fun VoiceChips(
    voices: List<String>,
    available: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(localizedText("all")) },
            colors = chipColors(selected == null)
        )
        voices.forEach { v ->
            val isSel = selected?.equals(v, ignoreCase = true) == true
            val isAvailable = available.any { it.equals(v, ignoreCase = true) }
            FilterChip(
                selected = isSel,
                enabled = isAvailable || isSel,
                onClick = { onSelect(if (isSel) null else v) },
                label = { Text(v) },
                colors = chipColors(isSel, isSel && !isAvailable)
            )
        }
    }
}

@Composable
private fun SeasonChips(
    seasons: List<Int>,
    available: Set<Int>,
    selected: Int?,
    onSelect: (Int?) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = selected == null,
            onClick = { onSelect(null) },
            label = { Text(localizedText("all")) },
            colors = chipColors(selected == null)
        )
        seasons.forEach { s ->
            val isSel = selected == s
            FilterChip(
                selected = isSel,
                enabled = s in available || isSel,
                onClick = { onSelect(if (isSel) null else s) },
                label = { Text(s.toString()) },
                colors = chipColors(isSel, isSel && s !in available)
            )
        }
    }
}

@Composable
private fun SortChips(
    selected: SortOption,
    descending: Boolean,
    onSortSelect: (SortOption) -> Unit,
    label: @Composable (SortOption) -> String
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
        SortOption.entries.forEach { option ->
            val isSel = selected == option
            FilterChip(
                selected = isSel,
                onClick = { onSortSelect(option) },
                label = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(label(option))
                        if (isSel) {
                            Icon(
                                imageVector = if (descending) Icons.Filled.ArrowDownward else Icons.Filled.ArrowUpward,
                                contentDescription = localizedText(if (descending) "sortDesc" else "sortAsc"),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                },
                colors = chipColors(isSel)
            )
        }
    }
}

@Composable
private fun chipColors(@Suppress("UNUSED_PARAMETER") selected: Boolean, unavailableSelected: Boolean = false): SelectableChipColors {
    val c = MaterialTheme.colorScheme
    return FilterChipDefaults.filterChipColors(
        containerColor = c.surface,
        labelColor = c.onSurface,
        selectedContainerColor = if (unavailableSelected) Color(0xFF756C4C) else c.primary,
        selectedLabelColor = if (unavailableSelected) Color(0xFFF1E4BE) else c.onPrimary
    )
}

@Composable
private fun sortLabel(option: SortOption): String = when (option) {
    SortOption.PEERS -> localizedText("sortByPeers")
    SortOption.SIZE -> localizedText("sortBySize")
    SortOption.QUALITY -> localizedText("sortByQuality")
}

@Composable
private fun SearchResultItem(result: SearchResult) {
    Surface(
        color = Color(0xFF243342),
        contentColor = MaterialTheme.colorScheme.onSurface,
        shape = MaterialTheme.shapes.medium,
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = result.title ?: localizedText("untitled"),
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            ResultMeta(result)
            if (result.audio.isNotEmpty()) {
                VoiceTags(result.audio)
            }
        }
    }
}

@Composable
private fun VoiceTags(audio: List<String>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        audio.forEach { voice ->
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.small,
            ) {
                Text(
                    text = voice,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }
    }
}

@Composable
private fun ResultMeta(result: SearchResult) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (result.quality != VideoQuality.UNKNOWN) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        text = qualityLabel(result.quality),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            result.size?.let {
                Text(
                    text = formatSize(it),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        val secondary = buildList {
            val categories = result.categories.map { categoryLabel(it) }
            if (categories.isNotEmpty()) add(categories.joinToString())
            if (result.seasons.isNotEmpty()) add(seasonsLabel(result))
            val label = localizedText("avgEpisode")
            result.avgEpisodeSeconds?.let { add("$label ${formatDuration(it)}") }
            val total = localizedText("totalTime")
            result.totalSeconds?.let { add("$total ${formatDuration(it)}") }
        }
        if (secondary.isNotEmpty()) {
            Text(
                text = secondary.joinToString("  ·  "),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        PeerStats(result.seeders, result.peers)
    }
}

@Composable
private fun PeerStats(seeders: Long?, peers: Long?) {
    if (seeders == null && peers == null) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (seeders != null) {
            Icon(
                imageVector = Icons.Filled.ArrowUpward,
                contentDescription = localizedText("seedersShort"),
                tint = Color(0xFF81C784),
                modifier = Modifier.size(14.dp)
            )
            Text(seeders.toString(), style = MaterialTheme.typography.bodyMedium, color = Color(0xFF81C784))
        }
        if (peers != null) {
            Icon(
                imageVector = Icons.Filled.ArrowDownward,
                contentDescription = localizedText("peersShort"),
                tint = Color(0xFFE57373),
                modifier = Modifier.size(14.dp)
            )
            Text(peers.toString(), style = MaterialTheme.typography.bodyMedium, color = Color(0xFFE57373))
        }
    }
}

@Composable
private fun seasonsLabel(result: SearchResult): String =
    "${localizedText("seasons")}: ${result.seasons.joinToString()}"

@Composable
private fun qualityLabel(quality: VideoQuality): String = when (quality) {
    VideoQuality.P720 -> "720p"
    VideoQuality.P1080 -> "1080p"
    VideoQuality.P2160 -> localizedText("quality4k")
    VideoQuality.UNKNOWN -> localizedText("unknown")
}

@Composable
private fun categoryLabel(value: String): String {
    val number = value.toIntOrNull() ?: return value
    val key = when (number / 1000) {
        1 -> "categoryConsole"
        2 -> "categoryMovies"
        3 -> "categoryAudio"
        4 -> "categoryPc"
        5 -> "categoryTv"
        6 -> "categoryAdult"
        7 -> "categoryBooks"
        8 -> "categoryOther"
        else -> return "${localizedText("category")} $value"
    }
    return localizedText(key)
}

private fun formatSize(bytes: Long): String {
    val units = listOf("B", "KiB", "MiB", "GiB", "TiB")
    var size = bytes.toDouble()
    var unit = 0
    while (size >= 1024 && unit < units.lastIndex) {
        size /= 1024
        unit++
    }
    return String.format(Locale.ROOT, "%.1f %s", size, units[unit])
}

private fun formatDuration(seconds: Long): String {
    val h = seconds / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return if (h > 0) "$h:${m.toString().padStart(2, '0')}:${s.toString().padStart(2, '0')}"
    else "$m:${s.toString().padStart(2, '0')}"
}
