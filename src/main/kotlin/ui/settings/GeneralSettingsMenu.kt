package com.github.korurg.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.github.korurg.configuration.ConfigurationService
import com.github.korurg.ui.localizedText
import org.koin.compose.koinInject

@Composable
fun GeneralSettingsMenu(

) {
    Column {
        Text(
            style = MaterialTheme.typography.headlineLarge,
            text = localizedText("generalSettings"),
            modifier = Modifier
                .padding(16.dp, 16.dp, 16.dp, 16.dp)
        )
        Text(
            text = localizedText("generalSettingsDescription"),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp)
        )

        //TODO: language
    }
}