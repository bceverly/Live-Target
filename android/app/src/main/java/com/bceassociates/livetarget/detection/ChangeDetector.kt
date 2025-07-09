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
import kotlin.math.max
import kotlin.math.min

/**
 * Detects changes in camera feed and identifies bullet impacts
 * Port of the iOS ChangeDetector algorithm to Android
 */
class ChangeDetector {
    companion object {
        private const val TAG = "ChangeDetector"
        private const val DEFAULT_THRESHOLD = 50
        private const val BYTES_PER_PIXEL = 4
    }

    private val _detectedChanges = MutableStateFlow<List<ChangePoint>>(emptyList())
    val detectedChanges: StateFlow<List<ChangePoint>> = _detectedChanges.asStateFlow()

    private val _isDetecting = MutableStateFlow(false)
    val isDetecting: StateFlow<Boolean> = _isDetecting.asStateFlow()

    private var previousBitmap: Bitmap? = null
    private val changeCounter = AtomicInteger(0)
    private var lastCheckTime = System.currentTimeMillis()
    private var checkInterval: Long = 2000L // 2 seconds in milliseconds
    private var minChangeSize: Int = 44 // Default: 22 caliber * 2

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

        // If detection is stopped, just update the previous image but don't detect
        if (!_isDetecting.value) {
            previousBitmap = newBitmap.copy(newBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            return
        }

        // Check if enough time has passed since last check
        if (currentTime - lastCheckTime < checkInterval) {
            return
        }

        val prevBitmap = previousBitmap
        if (prevBitmap == null) {
            previousBitmap = newBitmap.copy(newBitmap.config ?: Bitmap.Config.ARGB_8888, false)
            lastCheckTime = currentTime
            return
        }

        // Find differences between images
        val changes = findDifferences(prevBitmap, newBitmap)

        if (changes.isNotEmpty()) {
            val newChangePoint = ChangePoint(
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
     * Finds differences between two bitmaps and returns change points
     */
    private fun findDifferences(bitmap1: Bitmap, bitmap2: Bitmap): List<Point> {
        val width = min(bitmap1.width, bitmap2.width)
        val height = min(bitmap1.height, bitmap2.height)

        val pixels1 = IntArray(width * height)
        val pixels2 = IntArray(width * height)

        bitmap1.getPixels(pixels1, 0, width, 0, 0, width, height)
        bitmap2.getPixels(pixels2, 0, width, 0, 0, width, height)

        val differenceMap = createDifferenceMap(pixels1, pixels2, width, height)
        return findSignificantChanges(differenceMap, width, height)
    }

    /**
     * Creates a difference map between two pixel arrays
     */
    private fun createDifferenceMap(
        pixels1: IntArray,
        pixels2: IntArray,
        width: Int,
        height: Int,
    ): Array<BooleanArray> {
        val differenceMap = Array(height) { BooleanArray(width) }

        for (y in 0 until height) {
            for (x in 0 until width) {
                val index = y * width + x
                val pixel1 = pixels1[index]
                val pixel2 = pixels2[index]

                val red1 = (pixel1 shr 16) and 0xFF
                val green1 = (pixel1 shr 8) and 0xFF
                val blue1 = pixel1 and 0xFF

                val red2 = (pixel2 shr 16) and 0xFF
                val green2 = (pixel2 shr 8) and 0xFF
                val blue2 = pixel2 and 0xFF

                val diff = abs(red1 - red2) + abs(green1 - green2) + abs(blue1 - blue2)

                differenceMap[y][x] = diff > DEFAULT_THRESHOLD
            }
        }

        return differenceMap
    }

    /**
     * Finds significant changes in the difference map using flood fill
     */
    private fun findSignificantChanges(
        differenceMap: Array<BooleanArray>,
        width: Int,
        height: Int,
    ): List<Point> {
        val changes = mutableListOf<Point>()
        val visited = Array(height) { BooleanArray(width) }

        for (y in 0 until height) {
            for (x in 0 until width) {
                if (differenceMap[y][x] && !visited[y][x]) {
                    val boundingBox = floodFill(differenceMap, visited, x, y, width, height)

                    if (boundingBox.width >= minChangeSize && boundingBox.height >= minChangeSize) {
                        val centerX = (boundingBox.minX + boundingBox.width / 2f) / width
                        val centerY = (boundingBox.minY + boundingBox.height / 2f) / height
                        changes.add(Point(centerX, centerY))
                    }
                }
            }
        }

        return changes
    }

    /**
     * Represents a bounding box region
     */
    private data class BoundingBox(
        val minX: Int,
        val minY: Int,
        val maxX: Int,
        val maxY: Int,
    ) {
        val width: Int get() = maxX - minX + 1
        val height: Int get() = maxY - minY + 1
    }

    /**
     * Performs flood fill to find connected regions of changes
     */
    private fun floodFill(
        differenceMap: Array<BooleanArray>,
        visited: Array<BooleanArray>,
        startX: Int,
        startY: Int,
        width: Int,
        height: Int,
    ): BoundingBox {
        val stack = mutableListOf(Pair(startX, startY))
        var minX = startX
        var maxX = startX
        var minY = startY
        var maxY = startY

        while (stack.isNotEmpty()) {
            val (x, y) = stack.removeAt(stack.size - 1)

            // Check bounds and visited status
            if (x !in 0 until width || y !in 0 until height || visited[y][x] || !differenceMap[y][x]) {
                continue
            }

            visited[y][x] = true

            // Update bounding box
            minX = min(minX, x)
            maxX = max(maxX, x)
            minY = min(minY, y)
            maxY = max(maxY, y)

            // Add adjacent pixels to stack
            stack.addAll(
                listOf(
                    Pair(x + 1, y),
                    Pair(x - 1, y),
                    Pair(x, y + 1),
                    Pair(x, y - 1),
                ),
            )
        }

        return BoundingBox(minX, minY, maxX, maxY)
    }
}
