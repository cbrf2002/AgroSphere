package com.fsvdevs.agrosphere.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

object DensityHelper {
    private const val MAX_FONT_SCALE = 1.15f
    private const val MIN_FONT_SCALE = 0.9f
    private const val MAX_UI_SCALE = 2.8f
    private const val MIN_UI_SCALE = 1.5f

    @Composable
    fun getScaledDensity(): Density {
        val currentDensity = LocalDensity.current

        // Adjust font scale within min and max bounds
        val adjustedFontScale = when {
            currentDensity.fontScale > MAX_FONT_SCALE -> MAX_FONT_SCALE
            currentDensity.fontScale < MIN_FONT_SCALE -> MIN_FONT_SCALE
            else -> currentDensity.fontScale
        }

        // Adjust UI density within min and max bounds
        val adjustedDensity = when {
            currentDensity.density > MAX_UI_SCALE -> MAX_UI_SCALE
            currentDensity.density < MIN_UI_SCALE -> MIN_UI_SCALE
            else -> currentDensity.density
        }

        return Density(density = adjustedDensity, fontScale = adjustedFontScale)
    }
}