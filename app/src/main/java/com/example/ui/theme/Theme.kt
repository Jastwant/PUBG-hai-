package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = CyberCyan,
    onPrimary = DarkBg,
    primaryContainer = DarkElevated,
    onPrimaryContainer = CyberCyanLight,
    secondary = OceanBlue,
    onSecondary = TextPrimary,
    secondaryContainer = DarkElevated,
    onSecondaryContainer = CyberCyanLight,
    tertiary = CyberGold,
    onTertiary = DarkBg,
    background = DarkBg,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCard,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorderSubtle,
    outlineVariant = DarkBorder,
    error = CrimsonRed,
    onError = TextPrimary
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Esports experience default to premium dark
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

