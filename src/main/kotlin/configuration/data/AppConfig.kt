package com.github.korurg.configuration.data

import kotlinx.serialization.Serializable

@Serializable
data class AppConfig(
    val jacketConfig: JackettConfig = JackettConfig()
)
