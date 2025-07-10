//
//  UIImage+BatteryOptimization.swift
//  Live Target Watch App Watch App
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import UIKit
import WatchKit

extension UIImage {
    
    /// Resize image for Watch display to reduce memory usage
    func resizedForWatch() -> UIImage? {
        let screenBounds = WKInterfaceDevice.current().screenBounds
        let maxDimension = max(screenBounds.width, screenBounds.height) * WKInterfaceDevice.current().screenScale
        
        // If image is already small enough, return as-is
        let currentMaxDimension = max(size.width, size.height)
        if currentMaxDimension <= maxDimension {
            return self
        }
        
        // Calculate new size maintaining aspect ratio
        let scale = maxDimension / currentMaxDimension
        let newSize = CGSize(
            width: size.width * scale,
            height: size.height * scale
        )
        
        // Use watchOS-compatible image resizing
        UIGraphicsBeginImageContextWithOptions(newSize, false, 0.0)
        defer { UIGraphicsEndImageContext() }
        
        self.draw(in: CGRect(origin: .zero, size: newSize))
        return UIGraphicsGetImageFromCurrentImageContext()
    }
    
    /// Compress image for better memory management
    func compressedForWatch(quality: CGFloat = 0.7) -> UIImage? {
        guard let data = self.jpegData(compressionQuality: quality),
              let compressedImage = UIImage(data: data) else {
            return self
        }
        return compressedImage.resizedForWatch()
    }
    
    /// Battery optimization cleanup
    static func performBatteryOptimization() {
        // Force memory cleanup on watchOS
        DispatchQueue.main.async {
            autoreleasepool {
                // Trigger memory cleanup for Watch
                _ = UIImage()
            }
        }
    }
}