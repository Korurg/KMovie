package com.github.korurg.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import io.github.oshai.kotlinlogging.KotlinLogging
import java.util.Locale
import java.util.ResourceBundle

val LocalLanguage = compositionLocalOf { "en" }

private val logger = KotlinLogging.logger {}

@Composable
fun localizedText(key: String): String {
    val language = LocalLanguage.current
    val bundle = remember(language) {
        ResourceBundle.getBundle("i18n.messages", Locale.forLanguageTag(language))
    }
    return if (bundle.containsKey(key)) {
        bundle.getString(key)
    } else {
        logger.warn { "Localized text for key=$key not found" }
        "{$key}"
    }
}