package com.github.korurg.ui.settings

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import com.github.korurg.ui.localizedText

@Composable
fun GeneralSettingsMenu(
    language: String,
    onLanguageChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    SettingsPage(
        title = localizedText("generalSettings"),
        description = localizedText("generalSettingsDescription")
    ) {
        SettingsSection(title = localizedText("language")) {
            SettingsItem(
                title = localizedText("language"),
                description = localizedText("languageDescription")
            ) {
                OutlinedButton(onClick = { expanded = true }) {
                    Text(localizedText(if (language == "ru") "languageRussian" else "languageEnglish"))
                }
                DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                    DropdownMenuItem(text = { Text(localizedText("languageEnglish")) }, onClick = {
                        expanded = false
                        onLanguageChange("en")
                    })
                    DropdownMenuItem(text = { Text(localizedText("languageRussian")) }, onClick = {
                        expanded = false
                        onLanguageChange("ru")
                    })
                }
            }
        }
    }
}
