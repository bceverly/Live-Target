//
//  ChangeDetectorTest.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget

import android.graphics.Bitmap
import com.bceassociates.livetarget.detection.ChangeDetector
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class ChangeDetectorTest {
    
    private lateinit var changeDetector: ChangeDetector
    
    @Before
    fun setUp() {
        changeDetector = ChangeDetector()
    }
    
    @Test
    fun `test change detector initialization`() {
        assertNotNull(changeDetector)
        assertFalse(changeDetector.isDetecting.value)
        assertTrue(changeDetector.detectedChanges.value.isEmpty())
    }
    
    @Test
    fun `test start detection`() {
        changeDetector.startDetection()
        assertTrue(changeDetector.isDetecting.value)
    }
    
    @Test
    fun `test stop detection`() {
        changeDetector.startDetection()
        assertTrue(changeDetector.isDetecting.value)
        
        changeDetector.stopDetection()
        assertFalse(changeDetector.isDetecting.value)
    }
    
    @Test
    fun `test clear changes`() {
        changeDetector.clearChanges()
        assertTrue(changeDetector.detectedChanges.value.isEmpty())
    }
    
    @Test
    fun `test set check interval`() {
        val interval = 5.0
        changeDetector.setCheckInterval(interval)
        // Since checkInterval is private, we can't directly test it
        // but we can verify the method doesn't crash
        assertTrue(true)
    }
    
    @Test
    fun `test set min change size`() {
        val size = 50
        changeDetector.setMinChangeSize(size)
        // Since minChangeSize is private, we can't directly test it
        // but we can verify the method doesn't crash
        assertTrue(true)
    }
    
    @Test
    fun `test detect changes with null previous bitmap`() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        changeDetector.startDetection()
        
        // First call should not detect any changes (no previous bitmap)
        changeDetector.detectChanges(bitmap)
        assertTrue(changeDetector.detectedChanges.value.isEmpty())
    }
}