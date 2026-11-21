package com.github.korurg.configuration

import com.github.korurg.configuration.data.AppConfig
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

class ConfigurationService(
    val json: Json,
    val configurationPath: Path
) {
    var appConfig: AppConfig = loadOrCreateDefault(configurationPath)

    fun loadOrCreateDefault(configurationPath: Path): AppConfig {
        if (Files.exists(configurationPath)) {
            return load(configurationPath)
        }
        return init(configurationPath)
    }

    private fun init(configurationPath: Path): AppConfig {
        val appConfig = AppConfig()
        save(configurationPath, appConfig)
        return appConfig
    }

    fun save(configurationPath: Path = this.configurationPath, appConfig: AppConfig) {
        Files.writeString(configurationPath, json.encodeToString(AppConfig.serializer(), appConfig))
        this.appConfig = appConfig
    }

    private fun load(configurationPath: Path): AppConfig {
        val appConfig = json.decodeFromString<AppConfig>(Files.readString(configurationPath))
        return appConfig
    }
}