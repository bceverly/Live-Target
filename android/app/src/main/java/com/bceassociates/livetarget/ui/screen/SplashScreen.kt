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
                    
                    // Bullet holes with numbers
                    drawBulletHole(this, Offset(size.width * 0.5f, size.height * 0.45f), "1")
                    drawBulletHole(this, Offset(size.width * 0.58f, size.height * 0.5f), "2")
                    drawBulletHole(this, Offset(size.width * 0.42f, size.height * 0.5f), "3")
                }
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
        
        // Draw target rings from outside to inside
        val rings = listOf(
            maxRadius to Color.White,
            maxRadius * 0.8f to Color(0xFFDC143C), // Red
            maxRadius * 0.6f to Color.White,
            maxRadius * 0.4f to Color(0xFFDC143C), // Red
            maxRadius * 0.2f to Color.White
        )
        
        rings.forEach { (radius, color) ->
            drawCircle(
                color = color,
                radius = radius,
                center = center
            )
            drawCircle(
                color = Color.Black,
                radius = radius,
                center = center,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
            )
        }
        
        // Center bullseye
        drawCircle(
            color = Color(0xFFDC143C),
            radius = maxRadius * 0.1f,
            center = center
        )
    }
}

private fun drawBulletHole(drawScope: DrawScope, position: Offset, number: String) {
    with(drawScope) {
        // Black bullet hole
        drawCircle(
            color = Color.Black,
            radius = 8.dp.toPx(),
            center = position
        )
        
        // Red circle around bullet hole
        drawCircle(
            color = Color(0xFFDC143C),
            radius = 16.dp.toPx(),
            center = position,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3.dp.toPx())
        )
    }
}