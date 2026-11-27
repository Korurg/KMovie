package com.github.korurg.jackett.domain.native

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeRelease(
    @SerialName("Tracker")
    val tracker: String? = null,
    @SerialName("Details")
    val details: String? = null,
    @SerialName("Title")
    val title: String? = null,
    @SerialName("Size")
    val size: Long? = null,
    @SerialName("PublishDate")
    val publishDate: String? = null,
    @SerialName("Category")
    val category: List<Int>? = null,
    @SerialName("CategoryDesc")
    val categoryDesc: String? = null,
    @SerialName("Seeders")
    val seeders: Int? = null,
    @SerialName("Peers")
    val peers: Int? = null,
    @SerialName("MagnetUri")
    val magnetUri: String? = null,
    @SerialName("Link")
    val link: String? = null,
    @SerialName("Guid")
    val guid: String? = null,
    @SerialName("Languages")
    val languages: List<String>? = null,
    @SerialName("ffprobe")
    @Serializable(with = NativeProbeStreamListSerializer::class)
    val ffprobe: List<NativeProbeStream>? = null,
    @SerialName("info")
    val info: NativeInfo? = null
)
