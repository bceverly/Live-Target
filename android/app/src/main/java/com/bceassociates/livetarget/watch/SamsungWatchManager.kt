//
//  SamsungWatchManager.kt
//  Live Target Android
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.bceassociates.livetarget.watch

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import com.bceassociates.livetarget.data.model.ChangePoint
import com.samsung.android.sdk.accessory.SAAgentV2
import com.samsung.android.sdk.accessory.SAPeerAgent
import com.samsung.android.sdk.accessory.SASocket
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.IOException

/**
 * Samsung Galaxy Watch implementation of WatchConnectivityManager
 * Uses Samsung Accessory SDK for communication with Galaxy Watch devices
 */
class SamsungWatchManager(context: Context) : WatchConnectivityManager(context) {
    
    companion object {
        private const val TAG = "SamsungWatchManager"
        private const val LIVE_TARGET_CHANNEL_ID = 104
        private const val CONNECTIVITY_TEST_MESSAGE = "connectivityTest"
        private const val IMPACT_MESSAGE = "newImpact"
        private const val MAX_MESSAGE_SIZE = 1024 * 50 // 50KB limit for Samsung messages
    }
    
    private var socket: SASocket? = null
    private var peerAgent: SAPeerAgent? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    
    override fun initialize() {
        try {
            Log.d(TAG, "Initializing Samsung watch connectivity...")
            
            // For stub implementation, we'll simulate initialization
            // In a real implementation, this would initialize the Samsung Accessory SDK
            updateConnectionStatus(WatchConnectionStatus.UNKNOWN)
            
            Log.d(TAG, "Samsung watch connectivity initialized")
            
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize Samsung watch connectivity", e)
            updateConnectionStatus(WatchConnectionStatus.ERROR)
        }
    }
    
    override fun testWatchConnectivity() {
        scope.launch {
            try {
                Log.d(TAG, "Testing Samsung watch connectivity...")
                
                // For stub implementation, simulate connectivity test
                // In a real implementation, this would test actual Samsung Accessory SDK connection
                
                if (socket == null || peerAgent == null) {
                    Log.w(TAG, "No active connection to watch")
                    updateConnectionStatus(WatchConnectionStatus.DISCONNECTED)
                    return@launch
                }
                
                // Send connectivity test message
                val testMessage = JSONObject().apply {
                    put("type", CONNECTIVITY_TEST_MESSAGE)
                    put("timestamp", System.currentTimeMillis())
                }
                
                val success = sendMessage(testMessage.toString())
                if (success) {
                    Log.d(TAG, "Watch connectivity test successful")
                    updateConnectionStatus(WatchConnectionStatus.CONNECTED)
                } else {
                    Log.w(TAG, "Watch connectivity test failed")
                    updateConnectionStatus(WatchConnectionStatus.ERROR)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error testing watch connectivity", e)
                updateConnectionStatus(WatchConnectionStatus.ERROR)
            }
        }
    }
    
    override fun sendImpactToWatch(
        impact: ChangePoint,
        originalImage: Bitmap,
        circleColor: Int,
        numberColor: Int
    ) {
        scope.launch {
            try {
                Log.d(TAG, "Sending impact to Samsung watch: ${impact.number}")
                
                if (socket == null || peerAgent == null) {
                    Log.w(TAG, "No active connection to watch")
                    updateConnectionStatus(WatchConnectionStatus.ERROR)
                    return@launch
                }
                
                // Create zoomed impact image
                val zoomedImage = createZoomedImpactImage(originalImage, impact, circleColor, numberColor)
                
                // Compress image for transmission
                val imageData = compressImageForTransmission(zoomedImage)
                
                if (imageData.size > MAX_MESSAGE_SIZE) {
                    Log.w(TAG, "Image too large for transmission: ${imageData.size} bytes")
                    // Try with even smaller image
                    val smallerImage = resizeImage(zoomedImage, 100, 100)
                    val smallerData = compressImageForTransmission(smallerImage)
                    
                    if (smallerData.size > MAX_MESSAGE_SIZE) {
                        Log.e(TAG, "Even smaller image still too large, skipping")
                        return@launch
                    }
                    
                    sendImpactMessage(impact, smallerData)
                } else {
                    sendImpactMessage(impact, imageData)
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error sending impact to watch", e)
                updateConnectionStatus(WatchConnectionStatus.ERROR)
            }
        }
    }
    
    private fun sendImpactMessage(impact: ChangePoint, imageData: ByteArray) {
        try {
            val message = JSONObject().apply {
                put("type", IMPACT_MESSAGE)
                put("impactNumber", impact.number)
                put("timestamp", System.currentTimeMillis())
                put("locationX", impact.location.x)
                put("locationY", impact.location.y)
                // Note: Image data would be sent separately due to size limits
            }
            
            val success = sendMessage(message.toString())
            if (success) {
                Log.d(TAG, "Impact message sent successfully")
                updateConnectionStatus(WatchConnectionStatus.CONNECTED)
                
                // Send image data separately if needed
                // This is a simplified approach - in production, you'd chunk the data
                sendImageData(imageData)
            } else {
                Log.w(TAG, "Failed to send impact message")
                updateConnectionStatus(WatchConnectionStatus.ERROR)
            }
            
        } catch (e: Exception) {
            Log.e(TAG, "Error sending impact message", e)
            updateConnectionStatus(WatchConnectionStatus.ERROR)
        }
    }
    
    private fun sendImageData(imageData: ByteArray) {
        try {
            // For large data, we'd need to implement chunking
            // For now, just attempt to send if reasonably sized
            if (imageData.size <= MAX_MESSAGE_SIZE) {
                socket?.send(LIVE_TARGET_CHANNEL_ID, imageData)
                Log.d(TAG, "Image data sent to watch")
            } else {
                Log.w(TAG, "Image data too large for single message")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error sending image data", e)
        }
    }
    
    private fun sendMessage(message: String): Boolean {
        return try {
            socket?.send(LIVE_TARGET_CHANNEL_ID, message.toByteArray())
            true
        } catch (e: IOException) {
            Log.e(TAG, "Failed to send message", e)
            false
        }
    }
    
    private fun createZoomedImpactImage(
        originalImage: Bitmap,
        impact: ChangePoint,
        circleColor: Int,
        numberColor: Int
    ): Bitmap {
        val zoomFactor = 3.0f
        val cropSize = 200
        
        // Calculate crop rectangle around the impact
        val impactX = (impact.location.x * originalImage.width).toInt()
        val impactY = (impact.location.y * originalImage.height).toInt()
        
        val cropX = maxOf(0, impactX - cropSize / 2)
        val cropY = maxOf(0, impactY - cropSize / 2)
        val cropWidth = minOf(cropSize, originalImage.width - cropX)
        val cropHeight = minOf(cropSize, originalImage.height - cropY)
        
        // Create cropped bitmap
        val croppedBitmap = Bitmap.createBitmap(originalImage, cropX, cropY, cropWidth, cropHeight)
        
        // Create zoomed bitmap
        val zoomedWidth = (cropWidth * zoomFactor).toInt()
        val zoomedHeight = (cropHeight * zoomFactor).toInt()
        val zoomedBitmap = Bitmap.createScaledBitmap(croppedBitmap, zoomedWidth, zoomedHeight, true)
        
        // Create mutable bitmap for drawing
        val resultBitmap = zoomedBitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(resultBitmap)
        
        // Calculate circle position in zoomed coordinate system
        val relativeX = (impactX - cropX) * zoomFactor
        val relativeY = (impactY - cropY) * zoomFactor
        val circleRadius = 40f * zoomFactor / 3f
        
        // Draw circle
        val circlePaint = Paint().apply {
            color = circleColor
            style = Paint.Style.STROKE
            strokeWidth = 4f
            isAntiAlias = true
        }
        canvas.drawCircle(relativeX, relativeY, circleRadius, circlePaint)
        
        // Draw impact number
        val textPaint = Paint().apply {
            color = numberColor
            textSize = 32f * zoomFactor / 3f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            isFakeBoldText = true
        }
        
        val textY = relativeY + textPaint.textSize / 3f
        canvas.drawText(impact.number.toString(), relativeX, textY, textPaint)
        
        croppedBitmap.recycle()
        zoomedBitmap.recycle()
        
        return resultBitmap
    }
    
    private fun compressImageForTransmission(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return outputStream.toByteArray()
    }
    
    private fun resizeImage(bitmap: Bitmap, targetWidth: Int, targetHeight: Int): Bitmap {
        return Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)
    }
    
    override fun cleanup() {
        try {
            socket?.close()
            socket = null
            peerAgent = null
            Log.d(TAG, "Samsung watch connectivity cleaned up")
        } catch (e: Exception) {
            Log.e(TAG, "Error cleaning up Samsung watch connectivity", e)
        }
    }
    
    // Internal methods for handling Samsung Accessory SDK callbacks
    internal fun onPeerAgentFound(peerAgent: SAPeerAgent) {
        Log.d(TAG, "Samsung watch peer agent found")
        this.peerAgent = peerAgent
        updateWatchPaired(true)
        updateWatchAppInstalled(true)
    }
    
    internal fun onServiceConnectionResponse(socket: SASocket, result: Int) {
        Log.d(TAG, "Samsung watch service connection response: $result")
        if (result == 0) { // 0 = success
            this.socket = socket
            updateWatchConnected(true)
            updateConnectionStatus(WatchConnectionStatus.CONNECTED)
            Log.d(TAG, "Successfully connected to Samsung watch")
        } else {
            updateWatchConnected(false)
            updateConnectionStatus(WatchConnectionStatus.ERROR)
            Log.e(TAG, "Failed to connect to Samsung watch: $result")
        }
    }
    
    internal fun onServiceConnectionLost(reason: Int) {
        Log.d(TAG, "Samsung watch service connection lost: $reason")
        updateWatchConnected(false)
        updateConnectionStatus(WatchConnectionStatus.DISCONNECTED)
        socket = null
    }
    
    internal fun onMessageReceived(channelId: Int, data: ByteArray) {
        try {
            val message = String(data)
            Log.d(TAG, "Received message from Samsung watch: $message")
            
            val json = JSONObject(message)
            val type = json.optString("type")
            
            when (type) {
                CONNECTIVITY_TEST_MESSAGE -> {
                    Log.d(TAG, "Received connectivity test response from watch")
                    updateConnectionStatus(WatchConnectionStatus.CONNECTED)
                }
                else -> {
                    Log.d(TAG, "Received unknown message type: $type")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error processing message from watch", e)
        }
    }
}