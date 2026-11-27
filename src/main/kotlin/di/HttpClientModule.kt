package com.github.korurg.di

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.http.HttpHeaders
import org.koin.dsl.module
import org.koin.dsl.onClose

fun httpClientModule() = module {
    single {
        val hostHeaderPlugin = createClientPlugin("HostHeaderPlugin") {
            onRequest { request, _ ->
                val url = request.url
                val defaultPort = if (url.protocol.name == "https") 443 else 80
                request.headers[HttpHeaders.Host] = if (url.port == defaultPort) url.host else "${url.host}:${url.port}"
            }
        }

        HttpClient(CIO) {
            expectSuccess = true
            install(UserAgent) {
                agent = "KMovie/1.0"
            }
            install(hostHeaderPlugin)
        }
    } onClose { it?.close() }
}
