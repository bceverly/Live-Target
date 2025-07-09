//
//  SAAgentV2.kt
//  Live Target Android - Samsung Accessory SDK Stub
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

package com.samsung.android.sdk.accessory

/**
 * Stub implementation of Samsung Accessory SDK SAAgentV2 for CI/CD builds
 * This allows the project to build without the actual Samsung SDK
 */
abstract class SAAgentV2 {
    
    companion object {
        const val PEER_AGENT_FOUND = 0
        const val PEER_AGENT_NOT_FOUND = 1
    }
    
    open fun onCreate() {}
    
    open fun onDestroy() {}
    
    open fun onFindPeerAgentResponse(peerAgent: SAPeerAgent, result: Int) {}
    
    open fun onServiceConnectionResponse(peerAgent: SAPeerAgent, socket: SASocket, result: Int) {}
    
    open fun onServiceConnectionLost(peerAgent: SAPeerAgent, reason: Int) {}
    
    open fun onPeerAgentUpdated(peerAgent: SAPeerAgent, result: Int) {}
    
    open fun onAuthenticationResponse(peerAgent: SAPeerAgent, authToken: SAAuthenticationToken, result: Int) {}
    
    open fun onError(peerAgent: SAPeerAgent, errorMessage: String, errorCode: Int) {}
    
    protected fun requestServiceConnection(peerAgent: SAPeerAgent) {}
}