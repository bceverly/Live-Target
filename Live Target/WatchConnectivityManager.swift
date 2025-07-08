//
//  WatchConnectivityManager.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Foundation
import WatchConnectivity
import UIKit

class WatchConnectivityManager: NSObject, ObservableObject {
    static let shared = WatchConnectivityManager()
    
    @Published var isWatchConnected = false
    @Published var isWatchAppInstalled = false
    
    private override init() {
        super.init()
        
        if WCSession.isSupported() {
            WCSession.default.delegate = self
            WCSession.default.activate()
        }
    }
    
    func sendImpactToWatch(_ impact: ChangePoint, originalImage: UIImage, circleColor: UIColor, numberColor: UIColor) {
        guard WCSession.default.isReachable else {
            print("Watch is not reachable")
            return
        }
        
        // Create zoomed image around the impact
        let zoomedImage = createZoomedImpactImage(
            originalImage: originalImage,
            impact: impact,
            circleColor: circleColor,
            numberColor: numberColor
        )
        
        // Prepare data for watch
        guard let imageData = zoomedImage.jpegData(compressionQuality: 0.7) else {
            print("Failed to create image data")
            return
        }
        
        let message: [String: Any] = [
            "type": "newImpact",
            "impactNumber": impact.number,
            "timestamp": Date().timeIntervalSince1970,
            "imageData": imageData
        ]
        
        // Send to watch
        WCSession.default.sendMessage(message, replyHandler: { response in
            print("Watch acknowledged impact: \(response)")
        }, errorHandler: { error in
            print("Failed to send impact to watch: \(error.localizedDescription)")
        })
    }
    
    private func createZoomedImpactImage(originalImage: UIImage, impact: ChangePoint, circleColor: UIColor, numberColor: UIColor) -> UIImage {
        let imageSize = originalImage.size
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
        guard let croppedCGImage = originalImage.cgImage?.cropping(to: cropRect) else {
            return originalImage
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
}

extension WatchConnectivityManager: WCSessionDelegate {
    func session(_ session: WCSession, activationDidCompleteWith activationState: WCSessionActivationState, error: Error?) {
        DispatchQueue.main.async {
            self.isWatchConnected = activationState == .activated
            self.isWatchAppInstalled = session.isWatchAppInstalled
        }
        
        if let error = error {
            print("Watch session activation error: \(error.localizedDescription)")
        }
    }
    
    func sessionDidBecomeInactive(_ session: WCSession) {
        print("Watch session became inactive")
    }
    
    func sessionDidDeactivate(_ session: WCSession) {
        print("Watch session deactivated")
        WCSession.default.activate()
    }
    
    func sessionWatchStateDidChange(_ session: WCSession) {
        DispatchQueue.main.async {
            self.isWatchConnected = session.activationState == .activated
            self.isWatchAppInstalled = session.isWatchAppInstalled
        }
    }
    
    func session(_ session: WCSession, didReceiveMessage message: [String : Any], replyHandler: @escaping ([String : Any]) -> Void) {
        // Handle messages from watch if needed
        print("Received message from watch: \(message)")
        replyHandler(["status": "received"])
    }
}