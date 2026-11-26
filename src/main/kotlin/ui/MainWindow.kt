package com.github.korurg.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import com.github.korurg.ui.settings.SettingsMenu
import com.github.korurg.ui.sidepanel.SidePanel

val AppColorScheme = darkColorScheme(
    primary = Color(0xFFF39C12),
    onPrimary = Color(0xFF1C2833),

    secondary = Color(0xFFBDC3C7),
    onSecondary = Color(0xFF1C2833),

    tertiary = Color(0xFFF39C12),
    onTertiary = Color(0xFF1C2833),

    background = Color(0xFF1C2833),
    onBackground = Color(0xFFECF0F1),

    surface = Color(0xFF2C3E50),
    onSurface = Color(0xFFECF0F1),

    surfaceVariant = Color(0xFF34495E),
    onSurfaceVariant = Color(0xFFBDC3C7),

    outline = Color(0xFF566573),
    outlineVariant = Color(0xFF34495E),
)

private enum class Screen {
    HOME,
    SETTINGS
}

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    var language by remember { mutableStateOf("en") }
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    CompositionLocalProvider(LocalLanguage provides language) {
        Window(onCloseRequest = onCloseRequest, title = "KMovie") {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        SidePanel(
                            onSettingsClick = { currentScreen = Screen.SETTINGS },
                            onHomeClick = { currentScreen = Screen.HOME },
                        )
                        Column {
                            when (currentScreen) {
                                Screen.HOME -> Text("Привет, Compose!")
                                Screen.SETTINGS -> SettingsMenu()
                            }
                        }
                    }
                }
            }
        }
    }
}
