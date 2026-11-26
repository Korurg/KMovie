package com.github.korurg.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.github.korurg.configuration.ConfigurationService
import org.koin.compose.koinInject

private enum class ActiveSettingsMenu {
    GENERAL,
    AI,
    JACKET,
    TORSERVE,
    PROXY,
}

@Composable
fun SettingsMenu(

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
           when(activeSettingsMenu){
               ActiveSettingsMenu.GENERAL -> GeneralSettingsMenu()
               ActiveSettingsMenu.AI -> TODO()
               ActiveSettingsMenu.JACKET -> JacketSettingsMenu()
               ActiveSettingsMenu.TORSERVE -> TODO()
               ActiveSettingsMenu.PROXY -> TODO()
           }
        }
    }

}
