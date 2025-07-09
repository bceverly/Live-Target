//
//  WatchConnectivityManager.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Foundation
import WatchConnectivity
import UIKit
import os.log

enum WatchConnectionStatus {
    case unknown
    case connected
    case disconnected
    case error
}

class WatchConnectivityManager: NSObject, ObservableObject {
    static let shared = WatchConnectivityManager()
    
    @Published var isWatchConnected = false
    @Published var isWatchAppInstalled = false
    @Published var isWatchPaired = false
    @Published var watchConnectionStatus: WatchConnectionStatus = .unknown
    
    private let logger = Logger(subsystem: "com.bceassociates.Live-Target", category: "WatchConnectivity")
    
    private override init() {
        super.init()
        
        if WCSession.isSupported() {
            WCSession.default.delegate = self
            WCSession.default.activate()
        }
    }
    
    func testWatchConnectivity() {
        guard WCSession.isSupported() else {
            logger.warning("Watch connectivity not supported")
            DispatchQueue.main.async {
                self.watchConnectionStatus = .error
            }
            return
        }
        
        guard WCSession.default.activationState == .activated else {
            logger.warning("Watch session not activated")
            DispatchQueue.main.async {
                self.watchConnectionStatus = .disconnected
            }
            return
        }
        
        guard WCSession.default.isReachable else {
            logger.warning("Watch is not reachable")
            DispatchQueue.main.async {
                self.watchConnectionStatus = .disconnected
            }
            return
        }
        
        // Send a test message to verify connectivity
        let testMessage: [String: Any] = [
            "type": "connectivityTest",
            "timestamp": Date().timeIntervalSince1970
        ]
        
        WCSession.default.sendMessage(
            testMessage,
            replyHandler: { response in
                self.logger.info("Watch connectivity test successful: \(response)")
                DispatchQueue.main.async {
                    self.watchConnectionStatus = .connected
                }
            },
            errorHandler: { error in
                self.logger.error("Watch connectivity test failed: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    self.watchConnectionStatus = .error
                }
            }
        )
    }
    
    func sendImpactToWatch(_ impact: ChangePoint, originalImage: UIImage, circleColor: UIColor, numberColor: UIColor) {
        guard WCSession.default.isReachable else {
            logger.warning("Watch is not reachable")
            DispatchQueue.main.async {
                self.watchConnectionStatus = .error
            }
            return
        }
        
        // Create zoomed image around the impact
        let zoomedImage = createZoomedImpactImage(
            originalImage: originalImage,
            impact: impact,
            circleColor: circleColor,
            numberColor: numberColor
        )
        
        // Prepare data for watch - compress more aggressively for Watch
        guard let imageData = zoomedImage.jpegData(compressionQuality: 0.3) else {
            logger.error("Failed to create image data")
            return
        }
        
        // Check payload size (WatchConnectivity has ~65KB limit)
        if imageData.count > 60000 {
            logger.info("Image too large (\(imageData.count) bytes), creating smaller version")
            // Create an even smaller image
            let smallerImage = resizeImage(zoomedImage, targetSize: CGSize(width: 100, height: 100))
            guard let smallerData = smallerImage.jpegData(compressionQuality: 0.2) else {
                logger.error("Failed to create smaller image data")
                return
            }
            
            if smallerData.count > 60000 {
                logger.warning("Even smaller image still too large (\(smallerData.count) bytes), skipping")
                return
            }
            
            let message: [String: Any] = [
                "type": "newImpact",
                "impactNumber": impact.number,
                "timestamp": Date().timeIntervalSince1970,
                "imageData": smallerData
            ]
            
            sendMessageToWatch(message)
            return
        }
        
        let message: [String: Any] = [
            "type": "newImpact",
            "impactNumber": impact.number,
            "timestamp": Date().timeIntervalSince1970,
            "imageData": imageData
        ]
        
        sendMessageToWatch(message)
    }
    
    private func createZoomedImpactImage(originalImage: UIImage, impact: ChangePoint, circleColor: UIColor, numberColor: UIColor) -> UIImage {
        // First, rotate the image to correct orientation for Watch
        let rotatedImage = originalImage.rotated90DegreesClockwise()
        let imageSize = rotatedImage.size
        let zoomFactor: CGFloat = 3.0
        let cropSize: CGFloat = 200.0
        
        // Calculate crop rectangle around the impact
        let impactX = impact.location.x * imageSize.width
        let impactY = impact.location.y * imageSize.height
        
        let cropRect = CGRect(
            x: max(0, impactX - cropSize / 2),
            y: max(0, impactY - cropSize / 2),
            width: min(cropSize, imageSize.width - max(0, impactX - cropSize / 2)),
            height: min(cropSize, imageSize.height - max(0, impactY - cropSize / 2))
        )
        
        // Create cropped and zoomed image
        guard let croppedCGImage = rotatedImage.cgImage?.cropping(to: cropRect) else {
            return rotatedImage
        }
        
        let croppedImage = UIImage(cgImage: croppedCGImage)
        let zoomedSize = CGSize(width: cropRect.width * zoomFactor, height: cropRect.height * zoomFactor)
        
        let renderer = UIGraphicsImageRenderer(size: zoomedSize)
        
        return renderer.image { context in
            // Draw the zoomed background
            croppedImage.draw(in: CGRect(origin: .zero, size: zoomedSize))
            
            // Calculate circle position in the cropped/zoomed coordinate system
            let relativeX = (impactX - cropRect.minX) * zoomFactor
            let relativeY = (impactY - cropRect.minY) * zoomFactor
            
            let cgContext = context.cgContext
            let circleRadius: CGFloat = 40 * zoomFactor / 3.0
            
            // Draw circle
            cgContext.setStrokeColor(circleColor.cgColor)
            cgContext.setLineWidth(4)
            cgContext.addEllipse(in: CGRect(
                x: relativeX - circleRadius,
                y: relativeY - circleRadius,
                width: circleRadius * 2,
                height: circleRadius * 2
            ))
            cgContext.strokePath()
            
            // Draw number
            let numberString = "\(impact.number)"
            let fontSize = 32 * zoomFactor / 3.0
            let attributes: [NSAttributedString.Key: Any] = [
                .font: UIFont.systemFont(ofSize: fontSize, weight: .bold),
                .foregroundColor: numberColor
            ]
            
            let attributedString = NSAttributedString(string: numberString, attributes: attributes)
            let stringSize = attributedString.size()
            
            attributedString.draw(at: CGPoint(
                x: relativeX - stringSize.width / 2,
                y: relativeY - stringSize.height / 2
            ))
        }
    }
    
    private func sendMessageToWatch(_ message: [String: Any]) {
        WCSession.default.sendMessage(
            message,
            replyHandler: { response in
                self.logger.info("Watch acknowledged impact: \(response)")
                DispatchQueue.main.async {
                    self.watchConnectionStatus = .connected
                }
            },
            errorHandler: { error in
                self.logger.error("Failed to send impact to watch: \(error.localizedDescription)")
                DispatchQueue.main.async {
                    self.watchConnectionStatus = .error
                }
            }
        )
    }
    
    private func resizeImage(_ image: UIImage, targetSize: CGSize) -> UIImage {
        let renderer = UIGraphicsImageRenderer(size: targetSize)
        return renderer.image { _ in
            image.draw(in: CGRect(origin: .zero, size: targetSize))
        }
    }
}

extension WatchConnectivityManager: WCSessionDelegate {
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        DispatchQueue.main.async {
            self.isWatchConnected = activationState == .activated
            self.isWatchAppInstalled = session.isWatchAppInstalled
            self.isWatchPaired = session.isPaired
            
            // Don't automatically set connection status - wait for explicit connectivity test
            // Keep status as .unknown until testWatchConnectivity() is called
            if let error = error {
                self.watchConnectionStatus = .error
            }
            // Remove automatic status setting - let testWatchConnectivity() handle this
        }
        
        if let error = error {
            logger.error("Watch session activation error: \(error.localizedDescription)")
        }
    }
    
    func sessionDidBecomeInactive(_ session: WCSession) {
        logger.info("Watch session became inactive")
    }
    
    func sessionDidDeactivate(_ session: WCSession) {
        logger.info("Watch session deactivated")
        WCSession.default.activate()
    }
    
    func sessionWatchStateDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isWatchConnected = session.activationState == .activated
            self.isWatchAppInstalled = session.isWatchAppInstalled
            self.isWatchPaired = session.isPaired
            
            // Don't automatically change connection status - let testWatchConnectivity() handle this
            // Only reset to unknown if the session becomes inactive
            if session.activationState != .activated {
                self.watchConnectionStatus = .unknown
            }
        }
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String: Any], replyHandler: @escaping ([String: Any]) -> Void) {
        // Handle messages from watch if needed
        logger.info("Received message from watch: \(message)")
        replyHandler(["status": "received"])
    }
}
