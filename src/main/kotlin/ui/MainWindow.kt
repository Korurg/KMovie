package com.github.korurg.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.rememberWindowState
import com.github.korurg.ui.search.SearchPage
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
    SEARCH,
    SETTINGS
}

@Composable
fun MainWindow(onCloseRequest: () -> Unit) {
    var language by remember { mutableStateOf("en") }
    var currentScreen by remember { mutableStateOf(Screen.HOME) }

    CompositionLocalProvider(LocalLanguage provides language) {
        Window(
            onCloseRequest = onCloseRequest,
            title = "KMovie",
            state = rememberWindowState(placement = WindowPlacement.Maximized)
        ) {
            MaterialTheme(colorScheme = AppColorScheme) {
                Surface {
                    Row(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        SidePanel(
                            onSettingsClick = { currentScreen = Screen.SETTINGS },
                            onHomeClick = { currentScreen = Screen.HOME },
                            onSearchClick = { currentScreen = Screen.SEARCH },
                        )
                        Column {
                            when (currentScreen) {
                                Screen.HOME -> Text(localizedText("homeGreeting"))
                                Screen.SEARCH -> SearchPage()
                                Screen.SETTINGS -> SettingsMenu(
                                    language = language,
                                    onLanguageChange = { language = it })
                            }
                        }
                    }
                }
            }
        }
    }
}
