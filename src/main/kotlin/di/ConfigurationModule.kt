package com.github.korurg.di

import com.github.korurg.configuration.ConfigurationService
import kotlinx.serialization.json.Json
import org.koin.dsl.module
import java.nio.file.Path

fun configurationModule(
    configurationPath: Path = Path.of(System.getProperty("user.home"), ".kmovie", "config.json")
) = module {
    single(createdAtStart = true) {
        val json = get<Json>()
        val configurationService = ConfigurationService(json, configurationPath)

        configurationService
    }
}