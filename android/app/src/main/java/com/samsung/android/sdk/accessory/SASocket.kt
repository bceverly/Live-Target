//
//  SASocket.kt
//  Live Target Android - Samsung Accessory SDK Stub
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.samsung.android.sdk.accessory

/**
 * Stub implementation of Samsung Accessory SDK SASocket for CI/CD builds
 */
abstract class SASocket {
    
    open fun onReceive(channelId: Int, data: ByteArray) {}
    
    open fun onServiceConnectionLost(reason: Int) {}
    
    open fun onError(channelId: Int, errorMessage: String, errorCode: Int) {}
    
    fun send(channelId: Int, data: ByteArray) {}
    
    fun close() {}
}