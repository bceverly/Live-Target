//
//  ChangeDetector.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI
import Vision
import Photos
import os.log

/// Represents a detected change/impact point in the camera feed
struct ChangePoint {
    let id = UUID()
    /// Normalized coordinates (0-1) relative to image size
    let location: CGPoint
    /// Sequential number assigned to this impact
    let number: Int
}

/// Detects changes in camera feed and identifies bullet impacts
class ChangeDetector: ObservableObject {
    @Published var detectedChanges: [ChangePoint] = []
    @Published var isDetecting: Bool = false
    
    // MARK: - Private Properties
    private var previousImage: UIImage?
    private var changeCounter = 0
    private var lastCheckTime = Date()
    private var checkInterval: TimeInterval = 2.0
    private var minChangeSize: Int = 44  // Default: 22 caliber * 2
    
    private let logger = Logger(subsystem: "com.bceassociates.Live-Target", category: "ChangeDetector")
    
    // MARK: - Public Methods
    
    /// Sets the interval between change detection checks
    /// - Parameter interval: Time interval in seconds
    func setCheckInterval(_ interval: TimeInterval) {
        self.checkInterval = max(0.1, interval) // Minimum 0.1 seconds
    }
    
    /// Sets the minimum size for detected changes
    /// - Parameter size: Minimum size in pixels (typically caliber * 2)
    func setMinChangeSize(_ size: Int) {
        self.minChangeSize = max(1, size) // Minimum 1 pixel
    }
    
    /// Starts change detection
    func startDetection() {
        isDetecting = true
        previousImage = nil // Reset to get fresh baseline
        lastCheckTime = Date()
    }
    
    /// Stops change detection
    func stopDetection() {
        isDetecting = false
    }
    
    func detectChanges(in newImage: UIImage) {
        let currentTime = Date()
        
        // If detection is stopped, just update the previous image to keep it current
        // but don't perform any change detection
        if !isDetecting {
            self.previousImage = newImage
            return
        }
        
        guard currentTime.timeIntervalSince(lastCheckTime) >= checkInterval else {
            return
        }
        
        guard let previousImage = previousImage else {
            self.previousImage = newImage
            self.lastCheckTime = currentTime
            return
        }
        
        let changes = findDifferences(between: previousImage, and: newImage)
        
        if !changes.isEmpty {
            changeCounter += 1
            let changePoint = ChangePoint(
                location: changes.first ?? CGPoint(x: 0, y: 0),
                number: changeCounter
            )
            
            DispatchQueue.main.async {
                self.detectedChanges.append(changePoint)
                
                // Send to Apple Watch
                self.sendImpactToWatch(changePoint, image: newImage)
            }
        }
        
        self.previousImage = newImage
        self.lastCheckTime = currentTime
    }
    
    private func sendImpactToWatch(_ impact: ChangePoint, image: UIImage) {
        // Get current colors from UserDefaults
        let circleColorHex = UserDefaults.standard.string(forKey: "circleColor") ?? "FF0000"
        let numberColorHex = UserDefaults.standard.string(forKey: "numberColor") ?? "FF0000"
        
        let circleColor = UIColor(Color(hex: circleColorHex) ?? .red)
        let numberColor = UIColor(Color(hex: numberColorHex) ?? .red)
        
        // Send to watch
        WatchConnectivityManager.shared.sendImpactToWatch(
            impact,
            originalImage: image,
            circleColor: circleColor,
            numberColor: numberColor
        )
    }
    
    // MARK: - Private Methods
    
    /// Finds differences between two images and returns change points
    /// - Parameters:
    ///   - image1: First image to compare
    ///   - image2: Second image to compare
    /// - Returns: Array of normalized change points
    private func findDifferences(between image1: UIImage, and image2: UIImage) -> [CGPoint] {
        guard let cgImage1 = image1.cgImage,
              let cgImage2 = image2.cgImage else { return [] }
        
        let width = min(cgImage1.width, cgImage2.width)
        let height = min(cgImage1.height, cgImage2.height)
        
        guard let pixelData1 = extractPixelData(from: cgImage1, width: width, height: height),
              let pixelData2 = extractPixelData(from: cgImage2, width: width, height: height) else {
            return []
        }
        
        let differenceMap = createDifferenceMap(pixelData1: pixelData1, pixelData2: pixelData2, width: width, height: height)
        return findSignificantChanges(in: differenceMap, width: width, height: height)
    }
    
    /// Extracts pixel data from a CGImage
    private func extractPixelData(from cgImage: CGImage, width: Int, height: Int) -> [UInt8]? {
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        let bytesPerPixel = 4
        let bytesPerRow = width * bytesPerPixel
        let bitsPerComponent = 8
        
        var pixelData = [UInt8](repeating: 0, count: width * height * bytesPerPixel)
        
        guard let context = CGContext(
            data: &pixelData,
            width: width,
            height: height,
            bitsPerComponent: bitsPerComponent,
            bytesPerRow: bytesPerRow,
            space: colorSpace,
            bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue
        ) else {
            return nil
        }
        
        context.draw(cgImage, in: CGRect(x: 0, y: 0, width: width, height: height))
        return pixelData
    }
    
    /// Creates a difference map between two pixel data arrays
    private func createDifferenceMap(pixelData1: [UInt8], pixelData2: [UInt8], width: Int, height: Int) -> [[Bool]] {
        var differenceMap = Array(repeating: Array(repeating: false, count: width), count: height)
        let threshold: Int = 50
        let bytesPerPixel = 4
        
        for y in 0..<height {
            for x in 0..<width {
                let pixelIndex = (y * width + x) * bytesPerPixel
                
                guard pixelIndex + 3 < pixelData1.count else { continue }
                
                let red1 = Int(pixelData1[pixelIndex])
                let green1 = Int(pixelData1[pixelIndex + 1])
                let blue1 = Int(pixelData1[pixelIndex + 2])
                
                let red2 = Int(pixelData2[pixelIndex])
                let green2 = Int(pixelData2[pixelIndex + 1])
                let blue2 = Int(pixelData2[pixelIndex + 2])
                
                let diff = abs(red1 - red2) + abs(green1 - green2) + abs(blue1 - blue2)
                
                if diff > threshold {
                    differenceMap[y][x] = true
                }
            }
        }
        
        return differenceMap
    }
    
    /// Finds significant changes in the difference map
    private func findSignificantChanges(in differenceMap: [[Bool]], width: Int, height: Int) -> [CGPoint] {
        var changes: [CGPoint] = []
        var visited = Array(repeating: Array(repeating: false, count: width), count: height)
        
        for y in 0..<height {
            for x in 0..<width {
                if differenceMap[y][x] && !visited[y][x] {
                    let startPoint = CGPoint(x: x, y: y)
                    let imageSize = CGSize(width: width, height: height)
                    let region = floodFill(differenceMap: differenceMap, visited: &visited, startPoint: startPoint, imageSize: imageSize)
                    
                    if region.width >= minChangeSize && region.height >= minChangeSize {
                        let centerX = CGFloat(region.minX + region.width / 2) / CGFloat(width)
                        let centerY = CGFloat(region.minY + region.height / 2) / CGFloat(height)
                        changes.append(CGPoint(x: centerX, y: centerY))
                    }
                }
            }
        }
        
        return changes
    }
    
    /// Represents a bounding box region
    private struct BoundingBox {
        let minX: Int
        let minY: Int
        let maxX: Int
        let maxY: Int
        
        var width: Int { maxX - minX + 1 }
        var height: Int { maxY - minY + 1 }
    }
    
    /// Performs flood fill to find connected regions of changes
    /// - Parameters:
    ///   - differenceMap: Map of pixel differences
    ///   - visited: Tracking array for visited pixels
    ///   - startPoint: Starting coordinates
    ///   - imageSize: Size of the image
    /// - Returns: Bounding box of the connected region
    private func floodFill(differenceMap: [[Bool]], visited: inout [[Bool]], startPoint: CGPoint, imageSize: CGSize) -> BoundingBox {
        let startX = Int(startPoint.x)
        let startY = Int(startPoint.y)
        let width = Int(imageSize.width)
        let height = Int(imageSize.height)
        
        var stack = [(startX, startY)]
        var minX = startX, maxX = startX
        var minY = startY, maxY = startY
        
        while !stack.isEmpty {
            let (x, y) = stack.removeLast()
            
            // Check bounds and visited status
            guard x >= 0, x < width, y >= 0, y < height,
                  !visited[y][x], differenceMap[y][x] else {
                continue
            }
            
            visited[y][x] = true
            
            // Update bounding box
            minX = min(minX, x)
            maxX = max(maxX, x)
            minY = min(minY, y)
            maxY = max(maxY, y)
            
            // Add adjacent pixels to stack
            stack.append(contentsOf: [(x + 1, y), (x - 1, y), (x, y + 1), (x, y - 1)])
        }
        
        return BoundingBox(minX: minX, minY: minY, maxX: maxX, maxY: maxY)
    }
    
    /// Clears all detected changes and resets the counter
    func clearChanges() {
        detectedChanges.removeAll()
        changeCounter = 0
    }
    
    /// Saves the current image with detected changes to the photo library
    /// - Parameters:
    ///   - image: The base image to save
    ///   - circleColor: Color for impact circles
    ///   - numberColor: Color for impact numbers
    func saveCurrentImage(_ image: UIImage, circleColor: UIColor, numberColor: UIColor) {
        stopDetection() // Stop detection when saving
        
        // Create composite image with circles and numbers overlaid
        let compositeImage = createCompositeImage(baseImage: image, circleColor: circleColor, numberColor: numberColor)
        
        PHPhotoLibrary.requestAuthorization(for: .addOnly) { status in
            DispatchQueue.main.async {
                switch status {
                case .authorized, .limited:
                    self.saveImageToPhotoLibrary(compositeImage)
                case .denied, .restricted:
                    self.logger.error("Photo library access denied")
                case .notDetermined:
                    self.logger.warning("Photo library access not determined")
                @unknown default:
                    self.logger.error("Unknown photo library authorization status")
                }
            }
        }
    }
    
    /// Saves an image to the photo library
    /// - Parameter image: The image to save
    private func saveImageToPhotoLibrary(_ image: UIImage) {
        PHPhotoLibrary.shared().performChanges(
            {
                PHAssetChangeRequest.creationRequestForAsset(from: image)
            },
            completionHandler: { success, error in
                DispatchQueue.main.async {
                    if success {
                        self.logger.info("Image saved successfully to photo library")
                    } else if let error = error {
                        self.logger.error("Error saving image: \(error.localizedDescription)")
                    }
                }
            }
        )
    }
    
    /// Creates a composite image with detected changes overlaid
    /// - Parameters:
    ///   - baseImage: The base camera image
    ///   - circleColor: Color for impact circles
    ///   - numberColor: Color for impact numbers
    /// - Returns: Composite image with overlays
    private func createCompositeImage(baseImage: UIImage, circleColor: UIColor, numberColor: UIColor) -> UIImage {
        // For camera images, we need to rotate 90 degrees clockwise to match screen orientation
        let rotatedImage = baseImage.rotated90DegreesClockwise()
        let imageSize = rotatedImage.size
        let renderer = UIGraphicsImageRenderer(size: imageSize)
        
        return renderer.image { context in
            // Draw the rotated camera image
            rotatedImage.draw(in: CGRect(origin: .zero, size: imageSize))
            
            // Draw circles and numbers on top
            let cgContext = context.cgContext
            
            for change in detectedChanges {
                let centerX = change.location.x * imageSize.width
                let centerY = change.location.y * imageSize.height
                let circleRadius: CGFloat = 30
                
                // Draw circle
                cgContext.setStrokeColor(circleColor.cgColor)
                cgContext.setLineWidth(3)
                cgContext.addEllipse(in: CGRect(
                    x: centerX - circleRadius,
                    y: centerY - circleRadius,
                    width: circleRadius * 2,
                    height: circleRadius * 2
                ))
                cgContext.strokePath()
                
                // Draw number
                let numberString = "\(change.number)"
                let attributes: [NSAttributedString.Key: Any] = [
                    .font: UIFont.systemFont(ofSize: 24, weight: .bold),
                    .foregroundColor: numberColor
                ]
                
                let attributedString = NSAttributedString(string: numberString, attributes: attributes)
                let stringSize = attributedString.size()
                
                attributedString.draw(at: CGPoint(
                    x: centerX - stringSize.width / 2,
                    y: centerY - stringSize.height / 2
                ))
            }
        }
    }
}

// MARK: - UIImage Extension

extension UIImage {
    /// Rotates the image 90 degrees clockwise
    /// - Returns: Rotated image
    func rotated90DegreesClockwise() -> UIImage {
        guard let cgImage = cgImage else { return self }
        
        let newSize = CGSize(width: size.height, height: size.width)
        let renderer = UIGraphicsImageRenderer(size: newSize)
        
        return renderer.image { context in
            let cgContext = context.cgContext
            
            // Move to center, rotate 90 degrees counter-clockwise, and flip horizontally
            cgContext.translateBy(x: newSize.width / 2, y: newSize.height / 2)
            cgContext.rotate(by: -CGFloat.pi / 2)  // Counter-clockwise rotation
            cgContext.scaleBy(x: -1, y: 1)  // Flip horizontally to fix mirror effect
            
            // Draw the image centered
            cgContext.draw(cgImage, in: CGRect(
                x: -size.width / 2,
                y: -size.height / 2,
                width: size.width,
                height: size.height
            ))
        }
    }
}
