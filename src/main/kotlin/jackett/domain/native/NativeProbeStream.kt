package com.github.korurg.jackett.domain.native

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeProbeStream(
    @SerialName("index")
    val index: Int? = null,
    @SerialName("codec_name")
    val codecName: String? = null,
    @SerialName("codec_type")
    val codecType: String? = null,
    @SerialName("width")
    val width: Int? = null,
    @SerialName("height")
    val height: Int? = null,
    @SerialName("coded_width")
    val codedWidth: Int? = null,
    @SerialName("coded_height")
    val codedHeight: Int? = null,
    @SerialName("channels")
    val channels: Int? = null,
    @SerialName("tags")
    val tags: NativeProbeTags? = null
)
