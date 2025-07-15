//
//  MainScreen.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.screen

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bceassociates.livetarget.R
import com.bceassociates.livetarget.ui.component.CameraPreview
import com.bceassociates.livetarget.ui.component.WatchStatusIcon
import com.bceassociates.livetarget.ui.component.ZoomControl
import com.bceassociates.livetarget.ui.theme.LiveTargetTheme
import com.bceassociates.livetarget.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel = viewModel(),
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }

    var hasStoragePermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES,
                ) == PackageManager.PERMISSION_GRANTED
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                true // Pre-M doesn't need runtime permissions
            },
        )
    }

    var showSettings by remember { mutableStateOf(false) }
    var showHelp by remember { mutableStateOf(false) }

    val cameraLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            hasCameraPermission = isGranted
        }

    val storageLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { isGranted: Boolean ->
            hasStoragePermission = isGranted
        }

    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = modifier.fillMaxSize()) {
        // Top App Bar
        TopAppBar(
            title = { Text(stringResource(R.string.app_name)) },
            actions = {
                WatchStatusIcon(
                    status = uiState.watchConnectionStatus,
                    integrationEnabled = uiState.watchIntegrationEnabled,
                )
                IconButton(onClick = { showHelp = true }) {
                    Icon(Icons.Default.Info, contentDescription = stringResource(R.string.help))
                }
                IconButton(onClick = { showSettings = true }) {
                    Icon(Icons.Default.Settings, contentDescription = stringResource(R.string.settings))
                }
            },
        )

        // Main Content
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
        ) {
            if (hasCameraPermission) {
                // Camera Preview with Impact Overlays
                Box(modifier = Modifier.fillMaxSize()) {
                    CameraPreview(
                        onImageCaptured = { bitmap ->
                            viewModel.processImage(bitmap)
                        },
                        zoomFactor = uiState.zoomFactor.toFloat(),
                        onZoomCapabilitiesChanged = { minZoom, maxZoom ->
                            viewModel.updateZoomCapabilities(minZoom, maxZoom)
                        },
                        modifier = Modifier.fillMaxSize(),
                    )

                    // Impact overlays
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawImpacts(
                            impacts = uiState.detectedChanges,
                            circleColor = Color(android.graphics.Color.parseColor("#${uiState.circleColor}")),
                            numberColor = Color(android.graphics.Color.parseColor("#${uiState.numberColor}")),
                        )
                    }

                    // Status indicator
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.TopEnd)
                                .padding(16.dp),
                    ) {
                        Card(
                            colors =
                                CardDefaults.cardColors(
                                    containerColor =
                                        if (uiState.isDetecting) {
                                            MaterialTheme.colorScheme.errorContainer
                                        } else {
                                            MaterialTheme.colorScheme.secondaryContainer
                                        },
                                ),
                        ) {
                            Text(
                                text =
                                    if (uiState.isDetecting) {
                                        stringResource(R.string.detecting)
                                    } else {
                                        stringResource(R.string.stopped)
                                    },
                                modifier = Modifier.padding(8.dp),
                                color =
                                    if (uiState.isDetecting) {
                                        MaterialTheme.colorScheme.onErrorContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    },
                            )
                        }
                    }

                    // Zoom Control positioned at the bottom
                    Box(
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 100.dp),
                        // Space for bottom controls
                    ) {
                        ZoomControl(
                            zoomFactor = uiState.zoomFactor.toFloat(),
                            onZoomChange = { newZoom ->
                                Log.d("MainScreen", "ZoomControl onZoomChange: ${newZoom}x")
                                viewModel.setZoomFactor(newZoom.toDouble())
                            },
                            maxZoom = uiState.maxZoom,
                            minZoom = uiState.minZoom,
                        ).also {
                            Log.d(
                                "MainScreen",
                                "Creating ZoomControl with: zoom=${uiState.zoomFactor}x, range=${uiState.minZoom}x-${uiState.maxZoom}x",
                            )
                        }
                    }
                }
            } else {
                // Camera permission request
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = stringResource(R.string.camera_permission_required),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp),
                    )
                    Button(
                        onClick = { cameraLauncher.launch(Manifest.permission.CAMERA) },
                    ) {
                        Text(stringResource(R.string.grant_permission))
                    }
                }
            }
        }

        // Bottom Controls
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                Button(
                    onClick = { viewModel.clearChanges() },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary,
                        ),
                ) {
                    Text(stringResource(R.string.clear))
                }

                Button(
                    onClick = {
                        if (uiState.isDetecting) {
                            viewModel.stopDetection()
                        } else {
                            // Test watch connectivity when starting if integration is enabled
                            if (uiState.watchIntegrationEnabled) {
                                viewModel.testWatchConnectivity()
                            }
                            viewModel.startDetection()
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =
                                if (uiState.isDetecting) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                        ),
                ) {
                    Text(
                        if (uiState.isDetecting) {
                            stringResource(R.string.stop)
                        } else {
                            stringResource(R.string.start)
                        },
                    )
                }

                Button(
                    onClick = {
                        if (hasStoragePermission) {
                            viewModel.saveImage()
                            // Stop detection after saving
                            if (uiState.isDetecting) {
                                viewModel.stopDetection()
                            }
                        } else {
                            // Request storage permission
                            val permission =
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    Manifest.permission.READ_MEDIA_IMAGES
                                } else {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                }
                            storageLauncher.launch(permission)
                        }
                    },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary,
                        ),
                ) {
                    Text(stringResource(R.string.save))
                }
            }
        }
    }

    // Settings Screen
    if (showSettings) {
        SettingsScreen(
            onDismiss = { showSettings = false },
            viewModel = viewModel,
        )
    }

    // Help Screen
    if (showHelp) {
        HelpScreen(
            onDismiss = { showHelp = false },
        )
    }
}

private fun DrawScope.drawImpacts(
    impacts: List<com.bceassociates.livetarget.data.model.ChangePoint>,
    circleColor: Color,
    numberColor: Color,
) {
    impacts.forEach { impact ->
        val centerX = impact.location.x * size.width
        val centerY = impact.location.y * size.height
        val radius = 30.dp.toPx()

        // Draw circle
        drawCircle(
            color = circleColor,
            radius = radius,
            center = androidx.compose.ui.geometry.Offset(centerX, centerY),
            style = Stroke(width = 3.dp.toPx()),
        )

        // Draw impact number
        drawIntoCanvas { canvas ->
            val paint =
                android.graphics.Paint().apply {
                    color = numberColor.toArgb()
                    textSize = 24.dp.toPx()
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                    isFakeBoldText = true
                }

            canvas.nativeCanvas.drawText(
                impact.number.toString(),
                centerX,
                // Offset to center vertically
                centerY + paint.textSize / 3f,
                paint,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    LiveTargetTheme {
        MainScreen()
    }
}
