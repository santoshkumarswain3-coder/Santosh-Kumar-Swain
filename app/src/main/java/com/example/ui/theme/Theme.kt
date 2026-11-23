package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6BA3FF),
    onPrimary = Color(0xFF002563),
    primaryContainer = Color(0xFF17408C),
    onPrimaryContainer = Color(0xFFDBE7FF),
    secondary = SunnyGold,
    onSecondary = OnSunnyGold,
    secondaryContainer = Color(0xFF4A3500),
    onSecondaryContainer = SunnyGoldLight,
    tertiary = SkyAccent,
    onTertiary = Color(0xFF002438),
    tertiaryContainer = Color(0xFF0C4A6E),
    onTertiaryContainer = SkyContainer,
    background = DarkBg,
    onBackground = Color(0xFFF1F5F9),
    surface = DarkSurface,
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFCBD5E1),
    outline = DarkOutline,
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D),
    onErrorContainer = Color(0xFFFEE2E2)
)

private val LightColorScheme = lightColorScheme(
    primary = CobaltPrimary,
    onPrimary = Color.White,
    primaryContainer = CobaltContainer,
    onPrimaryContainer = OnCobaltContainer,
    secondary = SunnyGold,
    onSecondary = OnSunnyGold,
    secondaryContainer = SunnyGoldLight,
    onSecondaryContainer = OnSunnyGold,
    tertiary = SkyAccent,
    onTertiary = Color.White,
    tertiaryContainer = SkyContainer,
    onTertiaryContainer = OnSkyContainer,
    background = SoftCloud,
    onBackground = Color(0xFF0F172A),
    surface = SurfaceCard,
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFEEF3FB),
    onSurfaceVariant = Color(0xFF475569),
    outline = OutlineSubtle,
    error = CoralWrong,
    errorContainer = CoralContainer,
    onErrorContainer = OnCoralContainer
)

@Composable
fun BrightQuizTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
