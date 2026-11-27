package com.github.korurg.jackett.domain.native

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeSearchResponse(
    @SerialName("Results")
    val results: List<NativeRelease>,
    @SerialName("Indexers")
    val indexers: List<NativeIndexer>? = null,
    @SerialName("jacred")
    val jacred: Boolean? = null
)
