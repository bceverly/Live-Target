//
//  WatchConnectivityManager.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.watch

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bceassociates.livetarget.data.model.ChangePoint
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Abstract base class for watch connectivity management
 * Provides a unified interface for different watch platforms (Samsung, Wear OS)
 */
abstract class WatchConnectivityManager(protected val context: Context) {
    
    companion object {
        private const val TAG = "WatchConnectivityManager"
        
        @Volatile
        private var INSTANCE: WatchConnectivityManager? = null
        
        fun getInstance(context: Context): WatchConnectivityManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: createInstance(context).also { INSTANCE = it }
            }
        }
        
        private fun createInstance(context: Context): WatchConnectivityManager {
            return try {
                // Try to create the real Samsung implementation
                SamsungWatchManager(context)
            } catch (e: Exception) {
                // Fall back to stub implementation if Samsung SDK is not available
                Log.w(TAG, "Samsung SDK not available, using stub implementation", e)
                SamsungWatchManagerStub(context)
            }
        }
    }
    
    // Published state flows - similar to iOS @Published properties
    private val _isWatchConnected = MutableStateFlow(false)
    val isWatchConnected: StateFlow<Boolean> = _isWatchConnected.asStateFlow()
    
    private val _isWatchAppInstalled = MutableStateFlow(false)
    val isWatchAppInstalled: StateFlow<Boolean> = _isWatchAppInstalled.asStateFlow()
    
    private val _isWatchPaired = MutableStateFlow(false)
    val isWatchPaired: StateFlow<Boolean> = _isWatchPaired.asStateFlow()
    
    private val _watchConnectionStatus = MutableStateFlow(WatchConnectionStatus.UNKNOWN)
    val watchConnectionStatus: StateFlow<WatchConnectionStatus> = _watchConnectionStatus.asStateFlow()
    
    // Protected methods for subclasses to update state
    protected fun updateWatchConnected(connected: Boolean) {
        _isWatchConnected.value = connected
    }
    
    protected fun updateWatchAppInstalled(installed: Boolean) {
        _isWatchAppInstalled.value = installed
    }
    
    protected fun updateWatchPaired(paired: Boolean) {
        _isWatchPaired.value = paired
    }
    
    fun updateConnectionStatus(status: WatchConnectionStatus) {
        _watchConnectionStatus.value = status
    }
    
    // Abstract methods that subclasses must implement
    abstract fun initialize()
    abstract fun testWatchConnectivity()
    abstract fun sendImpactToWatch(
        impact: ChangePoint,
        originalImage: Bitmap,
        circleColor: Int,
        numberColor: Int
    )
    abstract fun cleanup()
}