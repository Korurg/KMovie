package com.github.korurg.di

import com.github.korurg.jackett.JackettClient
import org.koin.dsl.module

fun jackettModule() = module {
    single {
        JackettClient(get(), get())
    }
}
