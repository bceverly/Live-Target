import SwiftUI
import Vision
import Photos

struct ChangePoint {
    let id = UUID()
    let location: CGPoint
    let number: Int
}

class ChangeDetector: ObservableObject {
    @Published var detectedChanges: [ChangePoint] = []
    @Published var isDetecting: Bool = false
    private var previousImage: UIImage?
    private var changeCounter = 0
    private var lastCheckTime = Date()
    private var checkInterval: TimeInterval = 2.0
    private var minChangeSize: Int = 44  // Default: 22 * 2
    
    func setCheckInterval(_ interval: TimeInterval) {
        self.checkInterval = interval
    }
    
    func setMinChangeSize(_ size: Int) {
        self.minChangeSize = size
    }
    
    func startDetection() {
        isDetecting = true
        previousImage = nil // Reset to get fresh baseline
        lastCheckTime = Date()
    }
    
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
            }
        }
        
        self.previousImage = newImage
        self.lastCheckTime = currentTime
    }
    
    private func findDifferences(between image1: UIImage, and image2: UIImage) -> [CGPoint] {
        guard let cgImage1 = image1.cgImage,
              let cgImage2 = image2.cgImage else { return [] }
        
        let width = min(cgImage1.width, cgImage2.width)
        let height = min(cgImage1.height, cgImage2.height)
        
        let colorSpace = CGColorSpaceCreateDeviceRGB()
        let bytesPerPixel = 4
        let bytesPerRow = width * bytesPerPixel
        let bitsPerComponent = 8
        
        var pixelData1 = [UInt8](repeating: 0, count: width * height * bytesPerPixel)
        var pixelData2 = [UInt8](repeating: 0, count: width * height * bytesPerPixel)
        
        let context1 = CGContext(
            data: &pixelData1,
            width: width,
            height: height,
            bitsPerComponent: bitsPerComponent,
            bytesPerRow: bytesPerRow,
            space: colorSpace,
            bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue
        )
        
        let context2 = CGContext(
            data: &pixelData2,
            width: width,
            height: height,
            bitsPerComponent: bitsPerComponent,
            bytesPerRow: bytesPerRow,
            space: colorSpace,
            bitmapInfo: CGImageAlphaInfo.premultipliedLast.rawValue
        )
        
        context1?.draw(cgImage1, in: CGRect(x: 0, y: 0, width: width, height: height))
        context2?.draw(cgImage2, in: CGRect(x: 0, y: 0, width: width, height: height))
        
        let threshold: Int = 50
        
        // Create a difference map
        var differenceMap = Array(repeating: Array(repeating: false, count: width), count: height)
        
        for y in 0..<height {
            for x in 0..<width {
                let pixelIndex = (y * width + x) * bytesPerPixel
                
                if pixelIndex + 3 < pixelData1.count {
                    let r1 = Int(pixelData1[pixelIndex])
                    let g1 = Int(pixelData1[pixelIndex + 1])
                    let b1 = Int(pixelData1[pixelIndex + 2])
                    
                    let r2 = Int(pixelData2[pixelIndex])
                    let g2 = Int(pixelData2[pixelIndex + 1])
                    let b2 = Int(pixelData2[pixelIndex + 2])
                    
                    let diff = abs(r1 - r2) + abs(g1 - g2) + abs(b1 - b2)
                    
                    if diff > threshold {
                        differenceMap[y][x] = true
                    }
                }
            }
        }
        
        // Find contiguous regions of changes that are at least 40x40 pixels
        var changes: [CGPoint] = []
        var visited = Array(repeating: Array(repeating: false, count: width), count: height)
        
        for y in 0..<height {
            for x in 0..<width {
                if differenceMap[y][x] && !visited[y][x] {
                    let region = floodFill(differenceMap: differenceMap, visited: &visited, startX: x, startY: y, width: width, height: height)
                    
                    if region.width >= self.minChangeSize && region.height >= self.minChangeSize {
                        let centerX = CGFloat(region.minX + region.width / 2) / CGFloat(width)
                        let centerY = CGFloat(region.minY + region.height / 2) / CGFloat(height)
                        changes.append(CGPoint(x: centerX, y: centerY))
                    }
                }
            }
        }
        
        return changes
    }
    
    private func floodFill(differenceMap: [[Bool]], visited: inout [[Bool]], startX: Int, startY: Int, width: Int, height: Int) -> (minX: Int, minY: Int, maxX: Int, maxY: Int, width: Int, height: Int) {
        var stack = [(startX, startY)]
        var minX = startX, maxX = startX
        var minY = startY, maxY = startY
        
        while !stack.isEmpty {
            let (x, y) = stack.removeLast()
            
            if x < 0 || x >= width || y < 0 || y >= height || visited[y][x] || !differenceMap[y][x] {
                continue
            }
            
            visited[y][x] = true
            
            minX = min(minX, x)
            maxX = max(maxX, x)
            minY = min(minY, y)
            maxY = max(maxY, y)
            
            // Add adjacent pixels to stack
            stack.append((x + 1, y))
            stack.append((x - 1, y))
            stack.append((x, y + 1))
            stack.append((x, y - 1))
        }
        
        return (minX: minX, minY: minY, maxX: maxX, maxY: maxY, width: maxX - minX + 1, height: maxY - minY + 1)
    }
    
    func clearChanges() {
        detectedChanges.removeAll()
        changeCounter = 0
    }
    
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
                    print("Photo library access denied")
                case .notDetermined:
                    print("Photo library access not determined")
                @unknown default:
                    print("Unknown photo library authorization status")
                }
            }
        }
    }
    
    private func saveImageToPhotoLibrary(_ image: UIImage) {
        PHPhotoLibrary.shared().performChanges({
            PHAssetChangeRequest.creationRequestForAsset(from: image)
        }, completionHandler: { success, error in
            DispatchQueue.main.async {
                if success {
                    print("Image saved successfully to photo library")
                } else if let error = error {
                    print("Error saving image: \(error.localizedDescription)")
                }
            }
        })
    }
    
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

extension UIImage {
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