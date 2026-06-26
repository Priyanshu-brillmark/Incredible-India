package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = DarkTertiary,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onBackground = Color(0xFFF5ECE5),
    onSurface = Color(0xFFF5ECE5),
    surfaceVariant = DarkCard,
    onSurfaceVariant = Color(0xFFECE0D5)
)

private val LightColorScheme = lightColorScheme(
    primary = SaffronPrimary,
    secondary = IndiaGreen,
    tertiary = AshokaNavy,
    background = SandBackground,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = EarthyCharcoal,
    onSurface = EarthyCharcoal,
    surfaceVariant = WarmSandCard,
    onSurfaceVariant = Color(0xFF475569) // Modern slate-600
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep travel branding consistent
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
