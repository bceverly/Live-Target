//
//  WatchConnectivityManager.swift
//  Live Target Watch App Watch App
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Foundation
import WatchConnectivity
import UIKit
import WatchKit
import os.log

class WatchConnectivityManager: NSObject, ObservableObject {
    static let shared = WatchConnectivityManager()
    
    @Published var isPhoneConnected = false
    @Published var connectionStatus = "Disconnected"
    
    private let logger = Logger(subsystem: "com.bceassociates.Live-Target", category: "WatchConnectivity")
    
    override init() {
        super.init()
    }
    
    func activateSession() {
        if WCSession.isSupported() {
            WCSession.default.delegate = self
            WCSession.default.activate()
        }
    }
    
    func requestLatestImpact() {
        guard WCSession.default.isReachable else {
            logger.warning("Phone is not reachable")
            return
        }
        
        let message = ["type": "requestLatestImpact"]
        WCSession.default.sendMessage(
            message,
            replyHandler: { response in
                self.logger.info("Received latest impact response: \(response)")
            },
            errorHandler: { error in
                self.logger.error("Failed to request latest impact: \(error.localizedDescription)")
            }
        )
    }
}

extension WatchConnectivityManager: WCSessionDelegate {
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        DispatchQueue.main.async {
            self.isPhoneConnected = activationState == .activated && session.isReachable
            self.connectionStatus = activationState == .activated ? "Connected" : "Disconnected"
        }
        
        if let error = error {
            logger.error("Watch session activation error: \(error.localizedDescription)")
        }
    }
    
    func sessionReachabilityDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isPhoneConnected = session.isReachable
            self.connectionStatus = session.isReachable ? "Connected" : "Disconnected"
        }
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String: Any], replyHandler: @escaping ([String: Any]) -> Void) {
        logger.info("Received message from phone: \(message)")
        
        if let type = message["type"] as? String {
            switch type {
            case "newImpact":
                handleNewImpact(message: message)
                replyHandler(["status": "received"])
            default:
                replyHandler(["status": "unknown_type"])
            }
        }
    }
    
    private func handleNewImpact(message: [String: Any]) {
        guard let impactNumber = message["impactNumber"] as? Int,
              let timestamp = message["timestamp"] as? TimeInterval,
              let imageData = message["imageData"] as? Data,
              let image = UIImage(data: imageData) else {
            logger.error("Invalid impact data received")
            return
        }
        
        let impact = WatchImpact(
            number: impactNumber,
            timestamp: Date(timeIntervalSince1970: timestamp),
            zoomedImage: image
        )
        
        DispatchQueue.main.async {
            ImpactStore.shared.addImpact(impact)
            
            // Trigger haptic feedback
            WKInterfaceDevice.current().play(.notification)
        }
    }
}
