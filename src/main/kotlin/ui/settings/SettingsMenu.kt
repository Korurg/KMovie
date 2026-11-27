package com.github.korurg.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

private enum class ActiveSettingsMenu {
    GENERAL,
    AI,
    JACKET,
    TORSERVE,
    PROXY,
}

@Composable
fun SettingsMenu(
    language: String,
    onLanguageChange: (String) -> Unit
) {
    var activeSettingsMenu by remember { mutableStateOf(ActiveSettingsMenu.GENERAL) }

    Row(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
    ) {
        SidePanel(
            onGeneralSettingsClick = { activeSettingsMenu = ActiveSettingsMenu.GENERAL },
            onAiSettingsClick = { activeSettingsMenu = ActiveSettingsMenu.AI },
            onJacketSettingsClick = { activeSettingsMenu = ActiveSettingsMenu.JACKET }
        )
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth()
        ) {
            when (activeSettingsMenu) {
                ActiveSettingsMenu.GENERAL -> GeneralSettingsMenu(language, onLanguageChange)
                ActiveSettingsMenu.AI -> TODO()
                ActiveSettingsMenu.JACKET -> JacketSettingsMenu()
                ActiveSettingsMenu.TORSERVE -> TODO()
                ActiveSettingsMenu.PROXY -> TODO()
            }
        }
    }

}
