//
//  ChangePoint.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.data.model

import java.util.UUID

/**
 * Represents a detected change/impact point in the camera feed
 */
data class ChangePoint(
    /** Unique identifier for this change point */
    val id: String = UUID.randomUUID().toString(),
    /** Normalized coordinates (0-1) relative to image size */
    val location: Point,
    /** Sequential number assigned to this impact */
    val number: Int,
)

/**
 * Represents a point with normalized coordinates
 */
data class Point(
    val x: Float,
    val y: Float,
) {
    init {
        require(x in 0f..1f) { "X coordinate must be between 0 and 1" }
        require(y in 0f..1f) { "Y coordinate must be between 0 and 1" }
    }
}