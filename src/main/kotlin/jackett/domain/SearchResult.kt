package com.github.korurg.jackett.domain

data class SearchResult(
    val title: String?,
    val size: Long?,
    val quality: VideoQuality,
    val categories: List<String>,
    val audio: List<String>,
    val seasons: List<Int>,
    val seeders: Long?,
    val peers: Long?,
    val episodeCount: Int,
    val avgEpisodeSeconds: Long?,
    val totalSeconds: Long?
)
