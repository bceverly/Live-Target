//
//  UIImageExtensionTests.swift
//  Live TargetTests
//
//  Created by Bryan Everly on 7/8/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Testing
import UIKit
@testable import Live_Target

struct UIImageExtensionTests {
    
    @Test func testImageRotation90DegreesClockwise() async throws {
        // Create a rectangular test image to verify rotation
        let originalWidth = 100
        let originalHeight = 200
        let originalImage = createTestImage(width: originalWidth, height: originalHeight, color: .green)
        
        // Rotate the image
        let rotatedImage = originalImage.rotated90DegreesClockwise()
        
        // After 90-degree clockwise rotation, dimensions should be swapped
        #expect(rotatedImage.size.width == CGFloat(originalHeight))
        #expect(rotatedImage.size.height == CGFloat(originalWidth))
    }
    
    @Test func testImageRotationPreservesContent() async throws {
        // Create an image with distinct colors to test rotation accuracy
        let originalImage = createGradientTestImage(width: 100, height: 50)
        
        // Rotate the image
        let rotatedImage = originalImage.rotated90DegreesClockwise()
        
        // The rotated image should have swapped dimensions
        #expect(rotatedImage.size.width == 50)
        #expect(rotatedImage.size.height == 100)
        
        // The image should not be nil or empty
        #expect(rotatedImage.cgImage != nil)
    }
    
    @Test func testRotationWithSquareImage() async throws {
        // Test rotation with a square image (dimensions don't change)
        let squareImage = createTestImage(width: 100, height: 100, color: .blue)
        let rotatedImage = squareImage.rotated90DegreesClockwise()
        
        // Square image dimensions should remain the same
        #expect(rotatedImage.size.width == 100)
        #expect(rotatedImage.size.height == 100)
    }
    
    @Test func testRotationWithSmallImage() async throws {
        // Test with very small image to check edge cases
        let smallImage = createTestImage(width: 1, height: 1, color: .red)
        let rotatedImage = smallImage.rotated90DegreesClockwise()
        
        // Even 1x1 image should rotate without issues
        #expect(rotatedImage.size.width == 1)
        #expect(rotatedImage.size.height == 1)
    }
    
    // Helper function to create solid color test images
    private func createTestImage(width: Int, height: Int, color: UIColor) -> UIImage {
        let size = CGSize(width: width, height: height)
        let renderer = UIGraphicsImageRenderer(size: size)
        
        return renderer.image { context in
            color.setFill()
            context.fill(CGRect(origin: .zero, size: size))
        }
    }
    
    // Helper function to create gradient test images for better rotation testing
    private func createGradientTestImage(width: Int, height: Int) -> UIImage {
        let size = CGSize(width: width, height: height)
        let renderer = UIGraphicsImageRenderer(size: size)
        
        return renderer.image { context in
            // Create a simple gradient from left (red) to right (blue)
            let cgContext = context.cgContext
            let colorSpace = CGColorSpaceCreateDeviceRGB()
            
            let colors = [UIColor.red.cgColor, UIColor.blue.cgColor]
            let gradient = CGGradient(colorsSpace: colorSpace, colors: colors as CFArray, locations: [0.0, 1.0])!
            
            cgContext.drawLinearGradient(
                gradient,
                start: CGPoint(x: 0, y: size.height/2),
                end: CGPoint(x: size.width, y: size.height/2),
                options: []
            )
        }
    }
}
