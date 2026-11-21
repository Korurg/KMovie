package com.github.korurg

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.github.korurg.di.serializationModule
import com.github.korurg.di.configurationModule
import com.github.korurg.di.databaseModule
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

object Main {

    @JvmStatic
    fun main(args: Array<String>) {
        startKoin { modules(databaseModule(), configurationModule(), serializationModule()) }
        try {
            application {
                Window(onCloseRequest = ::exitApplication, title = "KMovie") {
                    MaterialTheme {
                        Text("Привет, Compose!")
                    }
                }
            }
        } finally {
            stopKoin()
        }
    }
}
