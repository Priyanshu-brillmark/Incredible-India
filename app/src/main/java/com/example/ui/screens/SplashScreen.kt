package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onNavigateNext: () -> Unit
) {
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1000)
        )
        delay(1500) // Keep visible
        onNavigateNext()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF4EB), // Soft Saffron Tint
                        Color(0xFFFFFFFF), // White
                        Color(0xFFEDFBF0)  // Soft Green Tint
                    )
                )
            )
            .testTag("splash_screen_root"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.alpha(alpha.value)
        ) {
            // High fidelity Ashoka Chakra drawn with Canvas
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(scale.value),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(120.dp)) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2
                    val navyBlue = Color(0xFF000080)

                    // Outer Circle
                    drawCircle(
                        color = navyBlue,
                        radius = radius,
                        style = Stroke(width = 6f)
                    )

                    // Inner Circle Hub
                    drawCircle(
                        color = navyBlue,
                        radius = radius * 0.15f
                    )

                    // 24 Spokes of Ashoka Chakra
                    for (i in 0 until 24) {
                        val angleRad = Math.toRadians((i * 15).toDouble())
                        val outerX = center.x + radius * cos(angleRad).toFloat()
                        val outerY = center.y + radius * sin(angleRad).toFloat()
                        
                        // Main Line
                        drawLine(
                            color = navyBlue,
                            start = center,
                            end = Offset(outerX, outerY),
                            strokeWidth = 3f
                        )

                        // Tiny spearhead/diamond curves at the end of each spoke
                        val arrowLength = radius * 0.08f
                        val arrowX1 = outerX - arrowLength * cos(angleRad + 0.2).toFloat()
                        val arrowY1 = outerY - arrowLength * sin(angleRad + 0.2).toFloat()
                        val arrowX2 = outerX - arrowLength * cos(angleRad - 0.2).toFloat()
                        val arrowY2 = outerY - arrowLength * sin(angleRad - 0.2).toFloat()

                        drawLine(color = navyBlue, start = Offset(outerX, outerY), end = Offset(arrowX1, arrowY1), strokeWidth = 2f)
                        drawLine(color = navyBlue, start = Offset(outerX, outerY), end = Offset(arrowX2, arrowY2), strokeWidth = 2f)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Incredible India",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A2240),
                    letterSpacing = 1.sp
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "B L O G S",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Light,
                    color = Color(0xFFFF9933),
                    letterSpacing = 6.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Discover beauty, culture & hidden gems",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF6E5D53),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                ),
                textAlign = TextAlign.Center
            )
        }
    }
}
