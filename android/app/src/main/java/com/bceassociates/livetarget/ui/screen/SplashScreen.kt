//
//  SplashScreen.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
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
    onSplashComplete: () -> Unit
) {
    val density = LocalDensity.current
    
    // Auto-advance after 3 seconds
    LaunchedEffect(Unit) {
        delay(3000)
        onSplashComplete()
    }
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .systemBarsPadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Target with bullet holes
            Box(
                modifier = Modifier.size(300.dp),
                contentAlignment = Alignment.Center
            ) {
                // Main target
                Canvas(
                    modifier = Modifier.fillMaxSize()
                ) {
                    drawTarget(this)
                    
                    // Bullet holes - positioned at 11 o'clock near outer edge like iOS
                    drawBulletHole(this, Offset(size.width * 0.42f, size.height * 0.25f), "1")   // 11 o'clock, outer ring
                    drawBulletHole(this, Offset(size.width * 0.47f, size.height * 0.22f), "2")   // 11 o'clock, slightly right
                    drawBulletHole(this, Offset(size.width * 0.37f, size.height * 0.28f), "3")   // 11 o'clock, slightly left
                }
                
                // Numbers positioned at 11 o'clock near outer edge
                Text(
                    text = "1",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(x = (-14).dp, y = (-87).dp)  // 11 o'clock position
                )
                Text(
                    text = "2",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(x = 16.dp, y = (-95).dp)     // 11 o'clock, slightly right
                )
                Text(
                    text = "3",
                    color = Color(0xFFDC143C),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .offset(x = (-44).dp, y = (-79).dp)  // 11 o'clock, slightly left
                )
            }
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // App title
            Text(
                text = "Live Target",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Subtitle
            Text(
                text = "Precision Shooting Assistant",
                color = Color.Gray,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
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
                center = center
            )
        }
        
        // Red bullseye like iOS
        drawCircle(
            color = Color(0xFFDC143C),
            radius = maxRadius * 0.15f,
            center = center
        )
        
        // White center in bullseye like iOS
        drawCircle(
            color = Color.White,
            radius = maxRadius * 0.075f,
            center = center
        )
    }
}

private fun drawBulletHole(drawScope: DrawScope, position: Offset, number: String) {
    with(drawScope) {
        // Red circle around bullet hole (larger like iOS)
        drawCircle(
            color = Color(0xFFDC143C),
            radius = 30.dp.toPx(),
            center = position,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
        
        // Black bullet hole
        drawCircle(
            color = Color.Black,
            radius = 8.dp.toPx(),
            center = position
        )
    }
}