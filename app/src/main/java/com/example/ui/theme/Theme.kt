package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = NaxiraLavender,
    onPrimary = NaxiraDarkBackground,
    primaryContainer = NaxiraSecondaryPurple,
    onPrimaryContainer = Color.White,
    secondary = NaxiraSecondaryPurple,
    onSecondary = Color.White,
    secondaryContainer = NaxiraDarkCard,
    onSecondaryContainer = NaxiraLavenderSoft,
    tertiary = NaxiraLavenderSoft,
    onTertiary = NaxiraDarkBackground,
    background = NaxiraDarkBackground,
    onBackground = Color.White,
    surface = NaxiraDarkSurface,
    onSurface = Color.White,
    surfaceVariant = NaxiraDarkCard,
    onSurfaceVariant = NaxiraLavenderSoft,
    outline = NaxiraSecondaryPurple.copy(alpha = 0.5f)
)

private val LightColorScheme = lightColorScheme(
    primary = NaxiraPrimaryPurple,
    onPrimary = Color.White,
    primaryContainer = NaxiraLavenderSoft,
    onPrimaryContainer = NaxiraPrimaryPurple,
    secondary = NaxiraSecondaryPurple,
    onSecondary = Color.White,
    secondaryContainer = NaxiraLightCard,
    onSecondaryContainer = NaxiraPrimaryPurple,
    tertiary = NaxiraSecondaryPurple,
    onTertiary = Color.White,
    background = NaxiraLightBackground,
    onBackground = Color(0xFF1E1430),
    surface = NaxiraLightSurface,
    onSurface = Color(0xFF1E1430),
    surfaceVariant = NaxiraLightCard,
    onSurfaceVariant = Color(0xFF4A3865),
    outline = NaxiraLavender.copy(alpha = 0.6f)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
