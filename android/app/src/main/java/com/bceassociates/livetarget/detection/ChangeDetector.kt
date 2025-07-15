//
//  ChangeDetector.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.detection

import android.graphics.Bitmap
import android.util.Log
import com.bceassociates.livetarget.data.model.ChangePoint
import com.bceassociates.livetarget.data.model.Point
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.abs
import kotlin.math.min

/**
 * Detects changes in camera feed and identifies bullet impacts
 * Port of the iOS ChangeDetector algorithm to Android
 */
class ChangeDetector {
    companion object {
        private const val TAG = "ChangeDetector"
        private const val DEFAULT_THRESHOLD = 8 // Lower threshold for better sensitivity
        private const val BYTES_PER_PIXEL = 4
    }

    private val _detectedChanges = MutableStateFlow<List<ChangePoint>>(emptyList())
    val detectedChanges: StateFlow<List<ChangePoint>> = _detectedChanges.asStateFlow()

    private val _isDetecting = MutableStateFlow(false)
    val isDetecting: StateFlow<Boolean> = _isDetecting.asStateFlow()

    private var previousBitmap: Bitmap? = null
    private val changeCounter = AtomicInteger(0)
    private var lastCheckTime = System.currentTimeMillis()
    private var checkInterval: Long = 1000L // 1 second for more stable detection
    private var minChangeSize: Int = 3 // Smaller for better detection

    // Grid-based detection parameters
    private var gridSquareSize: Int = 20 // Will be calculated based on caliber
    private var currentCaliberDiameter: Double = 0.223 // Default .22 LR
    private var currentZoomFactor: Double = 1.0

    /**
     * Sets the interval between change detection checks
     * @param intervalSeconds Time interval in seconds
     */
    fun setCheckInterval(intervalSeconds: Double) {
        checkInterval = (intervalSeconds * 1000).toLong().coerceAtLeast(100L)
    }

    /**
     * Sets the minimum size for detected changes
     * @param size Minimum size in pixels (typically caliber * 2)
     */
    fun setMinChangeSize(size: Int) {
        minChangeSize = size.coerceAtLeast(1)
    }

    /**
     * Sets the caliber parameters for calculating bullet hole size
     * @param diameterInches Bullet diameter in inches
     * @param zoomFactor Current zoom factor
     */
    fun setCaliberParameters(
        diameterInches: Double,
        zoomFactor: Double,
    ) {
        currentCaliberDiameter = diameterInches
        currentZoomFactor = zoomFactor
        updateGridSize()
    }

    /**
     * Calculates the expected pixel size of a bullet hole based on physics
     */
    private fun calculateBulletHolePixelSize(
        imageWidth: Int,
        imageHeight: Int,
    ): Int {
        // Assumptions for calculation:
        // - Typical camera: 12MP (4000x3000) with ~60° field of view
        // - Typical target distance: 25 yards (900 inches)
        // - Screen/preview dimensions will scale proportionally

        // Calculate pixels per inch based on image resolution and estimated field of view
        // Assume field of view covers approximately 36 inches at 25 yards (typical indoor range)
        val fieldOfViewInches = 36.0
        val pixelsPerInch = minOf(imageWidth, imageHeight) / fieldOfViewInches

        // Calculate bullet hole diameter in pixels (bullet diameter + some paper tear)
        val holeMultiplier = 1.2 // Bullet holes are typically 20% larger than bullet diameter
        val holeDiameterPixels = (currentCaliberDiameter * holeMultiplier * pixelsPerInch * currentZoomFactor).toInt()

        Log.d(TAG, "Calculated bullet hole size: ${currentCaliberDiameter}\" -> ${holeDiameterPixels}px (zoom: ${currentZoomFactor}x)")

        return holeDiameterPixels.coerceAtLeast(5) // Minimum 5 pixels
    }

    /**
     * Updates the grid square size based on current caliber and zoom
     */
    private fun updateGridSize() {
        // Grid squares should be 3x the expected bullet hole size for over-engineering
        val imageWidth = 1920 // Typical camera resolution width
        val imageHeight = 1080 // Typical camera resolution height
        val bulletHoleSize = calculateBulletHolePixelSize(imageWidth, imageHeight)
        gridSquareSize = (bulletHoleSize * 3).coerceAtLeast(10).coerceAtMost(100)
        Log.d(TAG, "Updated grid square size to: ${gridSquareSize}px")
    }

    /**
     * Starts change detection
     */
    fun startDetection() {
        _isDetecting.value = true
        previousBitmap = null // Reset to get fresh baseline
        lastCheckTime = System.currentTimeMillis()
        Log.d(TAG, "Change detection started")
    }

    /**
     * Stops change detection
     */
    fun stopDetection() {
        _isDetecting.value = false
        Log.d(TAG, "Change detection stopped")
    }

    /**
     * Detects changes in the provided image
     * @param newBitmap The current camera frame
     */
    fun detectChanges(newBitmap: Bitmap) {
        val currentTime = System.currentTimeMillis()
        Log.d(TAG, "detectChanges called - isDetecting: ${_isDetecting.value}, bitmap: ${newBitmap.width}x${newBitmap.height}")

        // If detection is stopped, just update the previous image but don't detect
        if (!_isDetecting.value) {
            Log.d(TAG, "Detection is stopped, updating previous bitmap only")
            previousBitmap = newBitmap.copy(newBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            return
        }

        // Check if enough time has passed since last check
        if (currentTime - lastCheckTime < checkInterval) {
            Log.d(TAG, "Too soon for next check: ${currentTime - lastCheckTime}ms < ${checkInterval}ms")
            return
        }

        val prevBitmap = previousBitmap
        if (prevBitmap == null) {
            Log.d(TAG, "No previous bitmap, setting baseline")
            previousBitmap = newBitmap.copy(newBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            lastCheckTime = currentTime
            return
        }

        // Find differences between images
        val changes = findDifferences(prevBitmap, newBitmap)

        Log.d(TAG, "Detection check - found ${changes.size} changes, threshold: $DEFAULT_THRESHOLD, minSize: $minChangeSize")
        Log.d(TAG, "Image comparison: prev=${prevBitmap.width}x${prevBitmap.height}, new=${newBitmap.width}x${newBitmap.height}")

        if (changes.isNotEmpty()) {
            val newChangePoint =
                ChangePoint(
                    location = changes.first(),
                    number = changeCounter.incrementAndGet(),
                )

            // Update the list of detected changes
            val currentChanges = _detectedChanges.value.toMutableList()
            currentChanges.add(newChangePoint)
            _detectedChanges.value = currentChanges

            Log.d(
                TAG,
                "Change detected at (${changes.first().x}, ${changes.first().y}), number: ${newChangePoint.number}",
            )
        }

        previousBitmap = newBitmap.copy(newBitmap.config ?: Bitmap.Config.ARGB_8888, false)
        lastCheckTime = currentTime
    }

    /**
     * Clears all detected changes and resets the counter
     */
    fun clearChanges() {
        _detectedChanges.value = emptyList()
        changeCounter.set(0)
        Log.d(TAG, "Changes cleared")
    }

    /**
     * Finds differences between two bitmaps using grid-based approach
     */
    private fun findDifferences(
        bitmap1: Bitmap,
        bitmap2: Bitmap,
    ): List<Point> {
        val width = min(bitmap1.width, bitmap2.width)
        val height = min(bitmap1.height, bitmap2.height)

        // Update grid size based on actual image dimensions
        updateGridSizeForImage(width, height)

        val pixels1 = IntArray(width * height)
        val pixels2 = IntArray(width * height)

        bitmap1.getPixels(pixels1, 0, width, 0, 0, width, height)
        bitmap2.getPixels(pixels2, 0, width, 0, 0, width, height)

        return findGridBasedChanges(pixels1, pixels2, width, height)
    }

    /**
     * Updates grid size based on actual image dimensions
     */
    private fun updateGridSizeForImage(
        imageWidth: Int,
        imageHeight: Int,
    ) {
        val bulletHoleSize = calculateBulletHolePixelSize(imageWidth, imageHeight)
        gridSquareSize = (bulletHoleSize * 3).coerceAtLeast(10).coerceAtMost(min(imageWidth, imageHeight) / 4)
        Log.d(TAG, "Grid size for ${imageWidth}x$imageHeight: ${gridSquareSize}px (bullet hole: ${bulletHoleSize}px)")
    }

    /**
     * Grid-based change detection with high sensitivity
     */
    private fun findGridBasedChanges(
        pixels1: IntArray,
        pixels2: IntArray,
        width: Int,
        height: Int,
    ): List<Point> {
        val candidateChanges = mutableListOf<Pair<Point, Int>>() // Point with difference score
        val significantThreshold = 50 // Higher threshold to avoid noise

        // Divide image into grid squares
        val gridWidth = width / gridSquareSize
        val gridHeight = height / gridSquareSize

        Log.d(TAG, "Grid analysis: ${gridWidth}x$gridHeight squares of ${gridSquareSize}px each")
        Log.d(TAG, "Image size: ${width}x$height, threshold: $significantThreshold")

        for (gridY in 0 until gridHeight) {
            for (gridX in 0 until gridWidth) {
                val startX = gridX * gridSquareSize
                val startY = gridY * gridSquareSize
                val endX = min(startX + gridSquareSize, width)
                val endY = min(startY + gridSquareSize, height)

                // Calculate average color difference in this grid square
                var totalDifference = 0
                var pixelCount = 0

                for (y in startY until endY) {
                    for (x in startX until endX) {
                        val index = y * width + x
                        if (index < pixels1.size && index < pixels2.size) {
                            val pixel1 = pixels1[index]
                            val pixel2 = pixels2[index]

                            val r1 = (pixel1 shr 16) and 0xFF
                            val g1 = (pixel1 shr 8) and 0xFF
                            val b1 = pixel1 and 0xFF

                            val r2 = (pixel2 shr 16) and 0xFF
                            val g2 = (pixel2 shr 8) and 0xFF
                            val b2 = pixel2 and 0xFF

                            val diff = kotlin.math.abs(r1 - r2) + kotlin.math.abs(g1 - g2) + kotlin.math.abs(b1 - b2)
                            totalDifference += diff
                            pixelCount++
                        }
                    }
                }

                if (pixelCount > 0) {
                    val averageDifference = totalDifference / pixelCount

                    // If this grid square shows significant change, add it as candidate
                    if (averageDifference > significantThreshold) {
                        val centerX = (startX + endX) / 2
                        val centerY = (startY + endY) / 2
                        val normalizedX = centerX.toFloat() / width
                        val normalizedY = centerY.toFloat() / height

                        candidateChanges.add(Pair(Point(normalizedX, normalizedY), averageDifference))

                        Log.d(
                            TAG,
                            "Significant change in grid ($gridX, $gridY) at pixel ($centerX, $centerY) - avg diff: $averageDifference",
                        )
                    }
                }
            }
        }

        // Sort by difference score and return only the most significant change
        val sortedChanges = candidateChanges.sortedByDescending { it.second }
        val finalChanges =
            if (sortedChanges.isNotEmpty()) {
                Log.d(TAG, "Most significant change has difference: ${sortedChanges.first().second}")
                listOf(sortedChanges.first().first) // Return only the most significant change
            } else {
                emptyList()
            }

        Log.d(TAG, "Grid analysis complete: ${candidateChanges.size} candidates found, returning ${finalChanges.size} changes")
        return finalChanges
    }
}
