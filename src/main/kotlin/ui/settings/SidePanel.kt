package com.github.korurg.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.DeveloperBoard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.korurg.ui.localizedText


@Composable
fun SidePanel(
    onGeneralSettingsClick: () -> Unit,
    onAiSettingsClick: () -> Unit,
    onJacketSettingsClick: () -> Unit,
) {
    var selectedButton by remember { mutableStateOf("generalSettings") }

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(200.dp)
            .background(Color(0xFF243342))
    ) {

        Text(
            text = localizedText("settings"),
            modifier = Modifier.absolutePadding(16.dp, 16.dp, 16.dp, 16.dp),
            style = MaterialTheme.typography.headlineSmall
        )

        SidePanelButton(
            imageVector = Icons.Default.Settings,
            contentDescription = localizedText("generalSettings"),
            selected = selectedButton == "generalSettings",
            onClick = {
                selectedButton = "generalSettings"
                onGeneralSettingsClick()
            }
        )
        SidePanelButton(
            imageVector = Icons.Default.DeveloperBoard,
            contentDescription = localizedText("aiSettings"),
            selected = selectedButton == "aiSettings",
            onClick = {
                selectedButton = "aiSettings"
                onAiSettingsClick()
            }
        )
        SidePanelButton(
            imageVector = Icons.Default.Dns,
            contentDescription = localizedText("jackettSettings"),
            selected = selectedButton == "jackettSettings",
            onClick = {
                selectedButton = "jackettSettings"
                onJacketSettingsClick()
            }
        )
    }

}

