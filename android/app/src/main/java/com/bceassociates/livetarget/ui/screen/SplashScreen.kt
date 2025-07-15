//
//  SplashScreen.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onSplashComplete: () -> Unit,
) {
    val density = LocalDensity.current

    // Auto-advance after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onSplashComplete()
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .background(Color.Black)
                .systemBarsPadding(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            // Target with bullet holes
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center,
            ) {
                // Main target
                Canvas(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    drawTarget(this)

                    // Bullet holes - spread out more horizontally and vertically
                    drawBulletHole(this, Offset(size.width * 0.35f, size.height * 0.20f), "1") // 11 o'clock, far left
                    drawBulletHole(this, Offset(size.width * 0.55f, size.height * 0.18f), "2") // 11 o'clock, far right
                    drawBulletHole(this, Offset(size.width * 0.45f, size.height * 0.35f), "3") // Lower center area
                }

                // Numbers positioned to match spread out bullet holes
                Text(
                    text = "1",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            // Far left position
                            .offset(x = (-54).dp, y = (-100).dp),
                )
                Text(
                    text = "2",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            // Far right position
                            .offset(x = 66.dp, y = (-108).dp),
                )
                Text(
                    text = "3",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            // Lower center position
                            .offset(x = (-6).dp, y = (-45).dp),
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // App title
            Text(
                text = "Live Target",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "Precision Shooting Assistant",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
            )
        }
    }
}

private fun drawTarget(drawScope: DrawScope) {
    with(drawScope) {
        val center = Offset(size.width / 2, size.height / 2)
        val maxRadius = size.minDimension / 2 * 0.9f

        // Draw target rings from outside to inside - alternating black and white like iOS
        for (ring in 0 until 6) {
            val radius = maxRadius * (1.0f - ring * 0.15f)
            val color = if (ring % 2 == 0) Color.White else Color.Black

            drawCircle(
                color = color,
                radius = radius,
                center = center,
            )
        }

        // Red bullseye like iOS
        drawCircle(
            color = Color(0xFFDC143C),
            radius = maxRadius * 0.15f,
            center = center,
        )

        // White center in bullseye like iOS
        drawCircle(
            color = Color.White,
            radius = maxRadius * 0.075f,
            center = center,
        )
    }
}

private fun drawBulletHole(
    drawScope: DrawScope,
    position: Offset,
    number: String,
) {
    with(drawScope) {
        // Red circle around bullet hole (larger like iOS)
        drawCircle(
            color = Color(0xFFDC143C),
            radius = 30.dp.toPx(),
            center = position,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx()),
        )

        // Red bullet hole
        drawCircle(
            color = Color(0xFFDC143C),
            radius = 8.dp.toPx(),
            center = position,
        )
    }
}
