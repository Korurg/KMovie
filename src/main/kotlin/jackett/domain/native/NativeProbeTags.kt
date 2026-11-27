package com.github.korurg.jackett.domain.native

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeProbeTags(
    @SerialName("language")
    val language: String? = null,
    @SerialName("DURATION")
    val duration: String? = null,
    @SerialName("title")
    val title: String? = null
)
