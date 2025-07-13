//
//  CameraPreview.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.component

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import java.io.ByteArrayOutputStream
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    onImageCaptured: (Bitmap) -> Unit,
    zoomFactor: Float = 1.0f,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }
    var camera by remember { mutableStateOf<Camera?>(null) }
    
    // Apply zoom when zoomFactor changes
    LaunchedEffect(zoomFactor, camera) {
        camera?.cameraControl?.setZoomRatio(zoomFactor)
    }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }

            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
            cameraProviderFuture.addListener(
                {
                    val cameraProvider = cameraProviderFuture.get()

                    // Preview use case
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                    // Image analysis use case for change detection
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setTargetResolution(android.util.Size(1280, 720)) // Higher resolution for better detection
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { analyzer ->
                            analyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                                processImage(imageProxy, onImageCaptured)
                            }
                        }

                    // Camera selector
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                    try {
                        // Unbind all use cases before rebinding
                        cameraProvider.unbindAll()

                        // Bind use cases to camera
                        val boundCamera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer,
                        )
                        
                        // Store camera reference for zoom control
                        camera = boundCamera
                        
                        // Set initial zoom
                        boundCamera.cameraControl.setZoomRatio(zoomFactor)

                        Log.d("CameraPreview", "Camera bound successfully")
                    } catch (exc: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", exc)
                    }
                },
                ContextCompat.getMainExecutor(ctx),
            )

            previewView
        },
        modifier = modifier.fillMaxSize(),
    )
}

private fun processImage(imageProxy: ImageProxy, onImageCaptured: (Bitmap) -> Unit) {
    try {
        // Convert ImageProxy to Bitmap
        val bitmap = imageProxyToBitmap(imageProxy)
        if (bitmap != null) {
            Log.d("CameraPreview", "Processed image: ${bitmap.width}x${bitmap.height}, format: ${imageProxy.format}")
            onImageCaptured(bitmap)
        } else {
            Log.w("CameraPreview", "Failed to convert imageProxy to bitmap")
        }
    } catch (e: Exception) {
        Log.e("CameraPreview", "Error processing image", e)
    } finally {
        imageProxy.close()
    }
}

private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
    return try {
        val image = imageProxy.image ?: return null
        val width = imageProxy.width
        val height = imageProxy.height

        when (imageProxy.format) {
            ImageFormat.YUV_420_888 -> {
                convertYuv420ToBitmap(image, width, height)
            }
            ImageFormat.NV21 -> {
                convertNv21ToBitmap(image, width, height)
            }
            else -> {
                Log.w("CameraPreview", "Unsupported image format: ${imageProxy.format}")
                // Create a simple bitmap as fallback
                Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            }
        }
    } catch (e: Exception) {
        Log.e("CameraPreview", "Error converting ImageProxy to Bitmap", e)
        null
    }
}

private fun convertYuv420ToBitmap(image: Image, width: Int, height: Int): Bitmap {
    val planes = image.planes
    val yPlane = planes[0]
    val uPlane = planes[1]
    val vPlane = planes[2]

    val yBuffer = yPlane.buffer
    val uBuffer = uPlane.buffer
    val vBuffer = vPlane.buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val yByteArray = ByteArray(ySize)
    val uByteArray = ByteArray(uSize)
    val vByteArray = ByteArray(vSize)

    yBuffer.get(yByteArray)
    uBuffer.get(uByteArray)
    vBuffer.get(vByteArray)

    // Convert to NV21 format for YuvImage
    val nv21 = ByteArray(ySize + uSize + vSize)
    System.arraycopy(yByteArray, 0, nv21, 0, ySize)

    // Interleave U and V bytes for NV21 format
    val uvPixelStride = uPlane.pixelStride
    if (uvPixelStride == 1) {
        // Contiguous U and V planes
        System.arraycopy(vByteArray, 0, nv21, ySize, vSize)
        System.arraycopy(uByteArray, 0, nv21, ySize + vSize, uSize)
    } else {
        // Interleaved U and V planes
        var pos = ySize
        val uvPixelCount = minOf(uSize / uvPixelStride, vSize / uvPixelStride)
        for (i in 0 until uvPixelCount) {
            val uvIndex = i * uvPixelStride
            if (uvIndex < vByteArray.size && uvIndex < uByteArray.size && pos + 1 < nv21.size) {
                nv21[pos] = vByteArray[uvIndex]
                nv21[pos + 1] = uByteArray[uvIndex]
                pos += 2
            }
        }
    }

    // Use YuvImage for reliable conversion
    val yuvImage = YuvImage(nv21, ImageFormat.NV21, width, height, null)
    val outputStream = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, width, height), 100, outputStream)
    val jpegArray = outputStream.toByteArray()
    return BitmapFactory.decodeByteArray(jpegArray, 0, jpegArray.size)
}

private fun convertNv21ToBitmap(image: Image, width: Int, height: Int): Bitmap {
    val nv21 = ByteArray(width * height * 3 / 2)
    val yBuffer = image.planes[0].buffer
    val uvBuffer = image.planes[1].buffer

    yBuffer.get(nv21, 0, width * height)
    uvBuffer.get(nv21, width * height, width * height / 2)

    return convertNv21ToBitmap(nv21, width, height)
}

private fun convertNv21ToBitmap(nv21: ByteArray, width: Int, height: Int): Bitmap {
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val pixels = IntArray(width * height)

    for (i in 0 until height) {
        for (j in 0 until width) {
            val yIndex = i * width + j
            val uvIndex = width * height + (i / 2) * width + (j and 0xFFFE)
            
            val y = nv21[yIndex].toInt() and 0xFF
            val v = nv21[uvIndex].toInt() and 0xFF
            val u = nv21[uvIndex + 1].toInt() and 0xFF

            // YUV to RGB conversion with proper centering
            val r = (y + 1.402f * (v - 128)).toInt().coerceIn(0, 255)
            val g = (y - 0.344f * (u - 128) - 0.714f * (v - 128)).toInt().coerceIn(0, 255)
            val b = (y + 1.772f * (u - 128)).toInt().coerceIn(0, 255)

            pixels[yIndex] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}