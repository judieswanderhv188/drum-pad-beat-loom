package com.tempo.beatloom.core.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val BeatLoomColors = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryInk,
    onPrimaryContainer = PrimarySoft,
    secondary = Accent,
    onSecondary = BgDeep,
    secondaryContainer = AccentInk,
    onSecondaryContainer = Accent,
    background = Bg,
    onBackground = TextPrimary,
    surface = SurfaceCard,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceAlt,
    onSurfaceVariant = TextSecondary,
    outline = Outline,
    outlineVariant = Outline,
    error = Danger,
    onError = Color.White
)

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = BeatLoomColors,
        typography = AppTypography,
        content = content
    )
}
