package com.github.korurg

import androidx.compose.ui.window.application
import com.github.korurg.di.*
import com.github.korurg.ui.MainWindow
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        startKoin {
            modules(
                databaseModule(),
                configurationModule(),
                serializationModule(),
                httpClientModule(),
                jackettModule()
            )
        }
        try {
            application {
                MainWindow(onCloseRequest = ::exitApplication)
            }
        } finally {
            stopKoin()
        }
    }
}
