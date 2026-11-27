package com.github.korurg.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.koin.dsl.module
import org.koin.dsl.onClose

fun httpClientModule() = module {
    single {
        HttpClient(CIO) {
            expectSuccess = true
        }
    } onClose { it?.close() }
}
