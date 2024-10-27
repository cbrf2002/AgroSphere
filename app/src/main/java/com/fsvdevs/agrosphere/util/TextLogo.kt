package com.fsvdevs.agrosphere.util

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.fsvdevs.agrosphere.R

@Composable
fun TextLogo(
    modifier: Modifier
) {
    Image(
        painter = painterResource(R.drawable.agrosphere_logowithtext),
        contentDescription = "AgroSphere Logo Text",
        modifier = modifier
    )
}