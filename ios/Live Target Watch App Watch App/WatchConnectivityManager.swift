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
    private var statusUpdateTimer: Timer?
    private var pendingStatusUpdate = false
    
    override init() {
        super.init()
        setupAppLifecycleNotifications()
    }
    
    deinit {
        statusUpdateTimer?.invalidate()
        deactivateSession()
        NotificationCenter.default.removeObserver(self)
    }
    
    private func setupAppLifecycleNotifications() {
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appDidEnterBackground),
            name: .NSExtensionHostDidEnterBackground,
            object: nil
        )
        
        NotificationCenter.default.addObserver(
            self,
            selector: #selector(appWillEnterForeground),
            name: .NSExtensionHostWillEnterForeground,
            object: nil
        )
    }
    
    @objc func appDidEnterBackground() {
        logger.info("App entering background - optimizing for battery")
        statusUpdateTimer?.invalidate()
        statusUpdateTimer = nil
        // Keep session active but reduce activity
    }
    
    @objc func appWillEnterForeground() {
        logger.info("App entering foreground - resuming full connectivity")
        if WCSession.default.activationState == .activated {
            debouncedStatusUpdate()
        }
    }
    
    func activateSession() {
        if WCSession.isSupported() {
            WCSession.default.delegate = self
            WCSession.default.activate()
            logger.info("Watch session activation requested")
        }
    }
    
    func deactivateSession() {
        // WatchConnectivity sessions should remain active for proper communication
        // but we can reduce update frequency when backgrounded
        logger.info("Reducing session activity for battery optimization")
    }
    
    private func debouncedStatusUpdate() {
        guard !pendingStatusUpdate else { return }
        
        pendingStatusUpdate = true
        statusUpdateTimer?.invalidate()
        
        statusUpdateTimer = Timer.scheduledTimer(withTimeInterval: 0.3, repeats: false) { _ in
            self.pendingStatusUpdate = false
            self.updateConnectionStatus()
        }
    }
    
    private func updateConnectionStatus() {
        let session = WCSession.default
        let isConnected = session.activationState == .activated && session.isReachable
        let status = isConnected ? "Connected" : "Disconnected"
        
        DispatchQueue.main.async {
            self.isPhoneConnected = isConnected
            self.connectionStatus = status
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
        if let error = error {
            logger.error("Watch session activation error: \(error.localizedDescription)")
        } else {
            logger.info("Watch session activation completed: \(activationState.rawValue)")
        }
        
        debouncedStatusUpdate()
    }
    
    func sessionReachabilityDidChange(_ session: WCSession) {
        logger.info("Session reachability changed: \(session.isReachable)")
        debouncedStatusUpdate()
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
        
        // Compress image for better battery performance
        let optimizedImage = image.compressedForWatch(quality: 0.8)
        
        let impact = WatchImpact(
            number: impactNumber,
            timestamp: Date(timeIntervalSince1970: timestamp),
            zoomedImage: optimizedImage
        )
        
        DispatchQueue.main.async {
            ImpactStore.shared.addImpact(impact)
            
            // Trigger lighter haptic feedback for battery optimization
            WKInterfaceDevice.current().play(.click)
        }
    }
}
