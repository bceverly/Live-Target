//
//  SamsungWatchManagerStub.kt
//  Live Target Android - Stub Implementation
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.watch

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.bceassociates.livetarget.data.model.ChangePoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Stub implementation of Samsung Galaxy Watch Manager for CI/CD builds
 * This allows the project to build and run without the actual Samsung Accessory SDK
 */
class SamsungWatchManagerStub(context: Context) : WatchConnectivityManager(context) {
    companion object {
        private const val TAG = "SamsungWatchManagerStub"
    }

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun initialize() {
        Log.d(TAG, "Initializing Samsung watch connectivity (stub implementation)")
        updateConnectionStatus(WatchConnectionStatus.UNKNOWN)
    }

    override fun testWatchConnectivity() {
        scope.launch {
            Log.d(TAG, "Testing Samsung watch connectivity (stub implementation)")

            // Simulate a connection test that always fails in stub mode
            updateConnectionStatus(WatchConnectionStatus.DISCONNECTED)

            Log.d(TAG, "Watch connectivity test completed (stub)")
        }
    }

    override fun sendImpactToWatch(
        impact: ChangePoint,
        originalImage: Bitmap,
        circleColor: Int,
        numberColor: Int,
    ) {
        scope.launch {
            Log.d(TAG, "Sending impact to watch (stub implementation)")
            Log.d(TAG, "Impact #${impact.number} at location (${impact.location.x}, ${impact.location.y})")

            // In stub mode, we just log the impact but don't actually send it
            Log.d(TAG, "Impact would be sent to Samsung Galaxy Watch if SDK was available")
        }
    }

    // Stub implementations for callback methods
    fun onPeerAgentFound(peerAgent: Any) {
        Log.d(TAG, "Peer agent found (stub)")
        updateConnectionStatus(WatchConnectionStatus.CONNECTED)
    }

    fun onServiceConnectionResponse(
        socket: Any,
        result: Int,
    ) {
        Log.d(TAG, "Service connection response: $result (stub)")
        if (result == 0) {
            updateConnectionStatus(WatchConnectionStatus.CONNECTED)
        } else {
            updateConnectionStatus(WatchConnectionStatus.ERROR)
        }
    }

    fun onServiceConnectionLost(reason: Int) {
        Log.d(TAG, "Service connection lost: $reason (stub)")
        updateConnectionStatus(WatchConnectionStatus.DISCONNECTED)
    }

    fun onMessageReceived(
        channelId: Int,
        data: ByteArray,
    ) {
        Log.d(TAG, "Message received on channel $channelId, size: ${data.size} (stub)")
    }

    override fun cleanup() {
        Log.d(TAG, "Cleaning up Samsung watch connectivity (stub implementation)")
    }
}
