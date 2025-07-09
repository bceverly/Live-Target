//
//  CameraPreview.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.ui.component

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
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
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

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
                        .setTargetResolution(android.util.Size(640, 480))
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
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalyzer,
                        )

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
            onImageCaptured(bitmap)
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
    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    val uvBuffer = ByteArray(uSize + vSize)
    vBuffer.get(uvBuffer, 0, vSize)
    uBuffer.get(uvBuffer, vSize, uSize)

    val uvPixelStride = planes[1].pixelStride
    if (uvPixelStride == 1) {
        System.arraycopy(uvBuffer, 0, nv21, ySize, uSize + vSize)
    } else {
        // Handle interleaved UV data
        var pos = ySize
        for (i in 0 until (uSize + vSize) step uvPixelStride) {
            nv21[pos++] = uvBuffer[i]
        }
    }

    return convertNv21ToBitmap(nv21, width, height)
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
            val y = nv21[i * width + j].toInt() and 0xFF
            val uvIndex = width * height + (i / 2) * width + (j and 0xFFFE.inv())
            val u = nv21[uvIndex].toInt() and 0xFF
            val v = nv21[uvIndex + 1].toInt() and 0xFF

            // YUV to RGB conversion
            val r = (y + 1.402 * (v - 128)).toInt().coerceIn(0, 255)
            val g = (y - 0.344 * (u - 128) - 0.714 * (v - 128)).toInt().coerceIn(0, 255)
            val b = (y + 1.772 * (u - 128)).toInt().coerceIn(0, 255)

            pixels[i * width + j] = (0xFF shl 24) or (r shl 16) or (g shl 8) or b
        }
    }

    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}