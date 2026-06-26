package com.example.desktop.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val SaffronPrimary = Color(0xFFFF9933)
val IndiaGreen = Color(0xFF138808)
val AshokaNavy = Color(0xFF0A2240)
val SandBackground = Color(0xFFFBF9F2)
val EarthyCharcoal = Color(0xFF0F172A)
val WarmSandCard = Color(0xFFFAF6F0)
val BentoBorderColor = Color(0xFFE6E2D8)

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
    onSurfaceVariant = Color(0xFF475569)
)

@Composable
fun DesktopTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        content = content
    )
}
