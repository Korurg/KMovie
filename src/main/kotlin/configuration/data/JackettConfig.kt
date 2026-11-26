package com.github.korurg.configuration.data

import kotlinx.serialization.Serializable

@Serializable
data class JackettConfig(
    val remoteJackettInstances: List<RemoteJackettInstance> = ArrayList(),
    val remoteJackettInstance: RemoteJackettInstance? = null
)

@Serializable
data class RemoteJackettInstance(
    val url: String
)
