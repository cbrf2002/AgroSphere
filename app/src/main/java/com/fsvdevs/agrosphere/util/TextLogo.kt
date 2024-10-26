package com.fsvdevs.agrosphere.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

@Composable
fun TextLogo() {
    val text = buildAnnotatedString {
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.secondary,
                fontSize = MaterialTheme.typography.displaySmall.fontSize
            )
        ) {
            append("AGRO")
        }
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Black,
            fontSize = MaterialTheme.typography.displaySmall.fontSize)
        ) {
            append("SPHERE")
        }
    }

    Text(text = text)
}