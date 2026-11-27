package com.github.korurg.di

import com.github.korurg.jackett.JacketNativeClient
import com.github.korurg.jackett.JacketTorznabClient
import com.github.korurg.jackett.SearchService
import org.koin.dsl.module

fun jackettModule() = module {
    single {
        JacketTorznabClient(get(), get())
    }
    single {
        JacketNativeClient(get(), get())
    }
    single { SearchService(get<com.github.korurg.configuration.ConfigurationService>(), get<JacketNativeClient>()) }
}
