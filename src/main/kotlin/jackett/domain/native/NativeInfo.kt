package com.github.korurg.jackett.domain.native

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NativeInfo(
    @SerialName("quality")
    val quality: Int? = null,
    @SerialName("videotype")
    val videotype: String? = null,
    @SerialName("voices")
    val voices: List<String>? = null,
    @SerialName("types")
    val types: List<String>? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("originalname")
    val originalname: String? = null,
    @SerialName("relased")
    val relased: Int? = null,
    @SerialName("sizeName")
    val sizeName: String? = null,
    @SerialName("seasons")
    val seasons: List<Int>? = null
)
