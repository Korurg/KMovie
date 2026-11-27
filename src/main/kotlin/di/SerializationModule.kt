package com.github.korurg.di

import kotlinx.serialization.json.Json
import nl.adaptivity.xmlutil.serialization.XML
import org.koin.dsl.module

fun serializationModule() = module {

    single(createdAtStart = true) {
        Json {
            prettyPrint = true
            encodeDefaults = true
            ignoreUnknownKeys = true
            coerceInputValues = true
        }
    }
    single(createdAtStart = true) {
        XML {
            defaultPolicy {
                ignoreUnknownChildren()
            }
        }
    }
}
