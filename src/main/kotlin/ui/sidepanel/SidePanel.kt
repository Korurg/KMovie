package com.github.korurg.ui.sidepanel

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.github.korurg.ui.Tooltip
import com.github.korurg.ui.localizedText

@Composable
fun SidePanel(
    onSettingsClick: () -> Unit,
    onHomeClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedButton by remember { mutableStateOf("home") }
    val panelWidth by animateDpAsState(
        targetValue = if (expanded) 200.dp else 52.dp,
        label = "sidePanelWidth"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(panelWidth)
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.Bottom
    ) {
        IconButton(onClick = { expanded = !expanded }) {
            Icon(
                imageVector = if (expanded) {
                    Icons.AutoMirrored.Filled.KeyboardArrowLeft
                } else {
                    Icons.AutoMirrored.Filled.KeyboardArrowRight
                },
                contentDescription = localizedText("expandSidePanel")
            )
        }


        SidePanelButton(
            Icons.Default.Home,
            localizedText("home"),
            expanded,
            selected = selectedButton == "home",
            onClick = {
                selectedButton = "home"
                onHomeClick.invoke()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        SidePanelButton(
            Icons.Default.Settings,
            localizedText("settings"),
            expanded,
            selected = selectedButton == "settings",
            onClick = {
                selectedButton = "settings"
                onSettingsClick.invoke()
            }
        )

    }
}
