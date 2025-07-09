//
//  SamsungWatchService.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.watch

import android.util.Log
import com.samsung.android.sdk.accessory.SAAgentV2
import com.samsung.android.sdk.accessory.SAAuthenticationToken
import com.samsung.android.sdk.accessory.SAPeerAgent
import com.samsung.android.sdk.accessory.SASocket

/**
 * Samsung Accessory Service for handling Galaxy Watch communication
 * This service manages the connection and message exchange with the watch app
 */
class SamsungWatchService : SAAgentV2() {
    
    companion object {
        private const val TAG = "SamsungWatchService"
        private const val LIVE_TARGET_CHANNEL_ID = 104
    }
    
    private lateinit var watchManager: SamsungWatchManager
    
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "Samsung watch service created")
        
        // Get the watch manager instance
        watchManager = WatchConnectivityManager.getInstance(this) as SamsungWatchManager
    }
    
    override fun onFindPeerAgentResponse(peerAgent: SAPeerAgent, result: Int) {
        Log.d(TAG, "Find peer agent response: $result")
        
        if (result == PEER_AGENT_FOUND) {
            Log.d(TAG, "Samsung watch peer agent found")
            watchManager.onPeerAgentFound(peerAgent)
            
            // Request service connection
            requestServiceConnection(peerAgent)
        } else {
            Log.w(TAG, "Samsung watch peer agent not found: $result")
        }
    }
    
    override fun onServiceConnectionResponse(peerAgent: SAPeerAgent, socket: SASocket, result: Int) {
        Log.d(TAG, "Service connection response: $result")
        watchManager.onServiceConnectionResponse(socket, result)
    }
    
    override fun onServiceConnectionLost(peerAgent: SAPeerAgent, reason: Int) {
        Log.d(TAG, "Service connection lost: $reason")
        watchManager.onServiceConnectionLost(reason)
    }
    
    override fun onPeerAgentUpdated(peerAgent: SAPeerAgent, result: Int) {
        Log.d(TAG, "Peer agent updated: $result")
        // Handle peer agent updates if needed
    }
    
    override fun onAuthenticationResponse(peerAgent: SAPeerAgent, authToken: SAAuthenticationToken, result: Int) {
        Log.d(TAG, "Authentication response: $result")
        // Handle authentication if needed
    }
    
    override fun onError(peerAgent: SAPeerAgent, errorMessage: String, errorCode: Int) {
        Log.e(TAG, "Samsung watch service error: $errorMessage (code: $errorCode)")
        watchManager.updateConnectionStatus(WatchConnectionStatus.ERROR)
    }
    
    /**
     * Socket implementation for handling data communication
     */
    class LiveTargetSocket : SASocket() {
        
        private lateinit var watchManager: SamsungWatchManager
        
        fun setWatchManager(manager: SamsungWatchManager) {
            this.watchManager = manager
        }
        
        override fun onReceive(channelId: Int, data: ByteArray) {
            Log.d(TAG, "Received data on channel $channelId, size: ${data.size}")
            
            if (::watchManager.isInitialized) {
                watchManager.onMessageReceived(channelId, data)
            } else {
                Log.w(TAG, "Watch manager not initialized, cannot process message")
            }
        }
        
        override fun onServiceConnectionLost(reason: Int) {
            Log.d(TAG, "Socket service connection lost: $reason")
            
            if (::watchManager.isInitialized) {
                watchManager.onServiceConnectionLost(reason)
            }
        }
        
        override fun onError(channelId: Int, errorMessage: String, errorCode: Int) {
            Log.e(TAG, "Socket error on channel $channelId: $errorMessage (code: $errorCode)")
            
            if (::watchManager.isInitialized) {
                watchManager.updateConnectionStatus(WatchConnectionStatus.ERROR)
            }
        }
    }
    
    override fun onDestroy() {
        Log.d(TAG, "Samsung watch service destroyed")
        super.onDestroy()
    }
}