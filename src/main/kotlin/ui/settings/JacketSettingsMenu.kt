package com.github.korurg.ui.settings

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.github.korurg.configuration.ConfigurationService
import com.github.korurg.configuration.data.RemoteJackettInstance
import com.github.korurg.ui.localizedText
import org.koin.compose.koinInject

@Composable
fun JacketSettingsMenu(
    configurationService: ConfigurationService = koinInject()
) {
    val config = configurationService.appConfig
    var jacketUrl by remember { mutableStateOf(config.jacketConfig.remoteJackettInstance?.url.orEmpty()) }
    var apiKey by remember { mutableStateOf(config.jacketConfig.remoteJackettInstance?.apiKey.orEmpty()) }

    SettingsPage(
        title = localizedText("jackettSettings"),
        description = localizedText("jacketSettingsDescription")
    ) {
        SettingsSection(title = localizedText("jacketConnection")) {
            SettingsItem(
                title = localizedText("jacketUrl"),
                description = localizedText("jacketUrlDescription")
            ) {
                OutlinedTextField(
                    value = jacketUrl,
                    onValueChange = { newUrl ->
                        jacketUrl = newUrl
                        configurationService.save(
                            appConfig = config.copy(
                                jacketConfig = config.jacketConfig.copy(
                                    remoteJackettInstance = RemoteJackettInstance(jacketUrl, apiKey)
                                )
                            )
                        )
                    },
                    placeholder = { Text("http://localhost:9117") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            SettingsItem(
                title = localizedText("jackettApiKey"),
                description = localizedText("jackettApiKeyDescription")
            ) {
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { newKey ->
                        apiKey = newKey
                        val current = configurationService.appConfig
                        configurationService.save(
                            appConfig = current.copy(
                                jacketConfig = current.jacketConfig.copy(
                                    remoteJackettInstance = RemoteJackettInstance(jacketUrl, newKey)
                                )
                            )
                        )
                    },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
