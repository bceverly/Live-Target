//
//  ZoomControl.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.component

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bceassociates.livetarget.ui.theme.LiveTargetTheme
import kotlin.math.abs

/**
 * Camera zoom control interface similar to the native Camera app
 */
@Composable
fun ZoomControl(
    zoomFactor: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxZoom: Float = 20.0f,
    minZoom: Float = 1.0f,
) {
    var showingZoomSlider by remember { mutableStateOf(false) }
    
    // Predefined zoom levels for quick access, filtered by hardware capabilities
    val baseZoomLevels = listOf(1.0f, 2.0f, 3.0f, 5.0f, 7.0f, 10.0f, 15.0f, 20.0f)
    
    // Add the actual hardware maxZoom if it's not in our predefined list and is reasonable
    // Also add intermediate levels (4x, 6x, 8x, 9x) if they fall within hardware range
    val extraLevels = mutableListOf<Float>()
    if (maxZoom >= 4.0f && !baseZoomLevels.contains(4.0f)) extraLevels.add(4.0f)
    if (maxZoom >= 6.0f && !baseZoomLevels.contains(6.0f)) extraLevels.add(6.0f) 
    if (maxZoom >= 8.0f && !baseZoomLevels.contains(8.0f)) extraLevels.add(8.0f)
    if (maxZoom >= 9.0f && !baseZoomLevels.contains(9.0f)) extraLevels.add(9.0f)
    
    // Add the exact hardware maxZoom if it's not already included and is reasonable
    if (maxZoom > 1.0f && maxZoom <= 20.0f && !baseZoomLevels.contains(maxZoom) && !extraLevels.contains(maxZoom)) {
        extraLevels.add(maxZoom)
    }
    
    val allZoomLevels = (baseZoomLevels + extraLevels).sorted()
    val zoomLevels = allZoomLevels.filter { it <= maxZoom && it >= minZoom }
    
    // Log zoom level filtering for debugging
    Log.d("ZoomControl", "Hardware zoom range: ${minZoom}x to ${maxZoom}x")
    Log.d("ZoomControl", "Available zoom levels: ${zoomLevels.joinToString(", ") { "${it}x" }}")
    Log.d("ZoomControl", "Current zoom factor: ${zoomFactor}x")
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showingZoomSlider) {
            ZoomSlider(
                zoomFactor = zoomFactor,
                onZoomChange = { newZoom ->
                    val clampedZoom = newZoom.coerceIn(minZoom, maxZoom)
                    Log.d("ZoomControl", "ZoomSlider changed: requested=${newZoom}x, clamped=${clampedZoom}x, range=${minZoom}x-${maxZoom}x")
                    onZoomChange(clampedZoom)
                },
                maxZoom = maxZoom,
                minZoom = minZoom,
            )
        }
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        ) {
            zoomLevels.forEach { level ->
                ZoomButton(
                    level = level,
                    currentZoom = zoomFactor,
                    onZoomChange = { clampedLevel ->
                        val finalLevel = clampedLevel.coerceIn(minZoom, maxZoom)
                        Log.d("ZoomControl", "ZoomButton clicked: requested=${clampedLevel}x, clamped=${finalLevel}x, range=${minZoom}x-${maxZoom}x")
                        onZoomChange(finalLevel)
                    },
                )
            }
            
            // Toggle slider button
            IconButton(
                onClick = { showingZoomSlider = !showingZoomSlider },
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = CircleShape,
                    )
                    .border(
                        width = 1.dp,
                        color = Color.White.copy(alpha = 0.3f),
                        shape = CircleShape,
                    ),
            ) {
                Icon(
                    imageVector = if (showingZoomSlider) Icons.Default.KeyboardArrowUp else Icons.Default.Add,
                    contentDescription = if (showingZoomSlider) "Hide zoom slider" else "Show zoom slider",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}

@Composable
private fun ZoomButton(
    level: Float,
    currentZoom: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isSelected = abs(currentZoom - level) < 0.1f
    
    val scale by animateFloatAsState(
        targetValue = if (isSelected) 1.1f else 1.0f,
        animationSpec = tween(durationMillis = 200),
        label = "zoom_button_scale",
    )
    
    Button(
        onClick = { onZoomChange(level) },
        modifier = modifier
            .size(40.dp)
            .scale(scale),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color.Yellow else Color.Black.copy(alpha = 0.6f),
            contentColor = if (isSelected) Color.Black else Color.White,
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Text(
            text = if (level == 1.0f) "1×" else String.format("%.1f×", level),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun ZoomSlider(
    zoomFactor: Float,
    onZoomChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    maxZoom: Float = 20.0f,
    minZoom: Float = 1.0f,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.3f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "${minZoom.toInt()}×",
                    color = Color.White,
                    fontSize = 12.sp,
                )
                
                Slider(
                    value = zoomFactor,
                    onValueChange = onZoomChange,
                    valueRange = minZoom..maxZoom,
                    steps = ((maxZoom - minZoom) * 10).toInt(), // 0.1 step increments
                    modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = Color.Yellow,
                        activeTrackColor = Color.Yellow,
                        inactiveTrackColor = Color.White.copy(alpha = 0.3f),
                    ),
                )
                
                Text(
                    text = "${maxZoom.toInt()}×",
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }
            
            // Current zoom indicator
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(
                    text = String.format("%.1f×", zoomFactor),
                    color = Color.White,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF808080)
@Composable
fun ZoomControlPreview() {
    LiveTargetTheme {
        var zoomFactor by remember { mutableStateOf(2.0f) }
        
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(16.dp),
        ) {
            ZoomControl(
                zoomFactor = zoomFactor,
                onZoomChange = { zoomFactor = it },
            )
        }
    }
}