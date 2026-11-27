package com.github.korurg.jackett.domain.native

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeIndexer(
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("Error")
    val error: String? = null
)
