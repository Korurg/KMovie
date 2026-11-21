package com.github.korurg.di

import kotlinx.serialization.json.Json
import org.koin.dsl.module

fun serializationModule() = module {

    single(createdAtStart = true) {
        Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
        }
    }
}