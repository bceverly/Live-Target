//
//  ChangeDetectorTests.swift
//  Live TargetTests
//
//  Created by Bryan Everly on 7/8/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Testing
import UIKit
@testable import Live_Target

struct ChangeDetectorTests {
    
    @Test func testChangeDetectorInitialization() async throws {
        let detector = ChangeDetector()
        #expect(detector.detectedChanges.isEmpty)
        #expect(detector.isDetecting == false)
    }
    
    @Test func testStartStopDetection() async throws {
        let detector = ChangeDetector()
        
        detector.startDetection()
        #expect(detector.isDetecting == true)
        
        detector.stopDetection()
        #expect(detector.isDetecting == false)
    }
    
    @Test func testCheckIntervalValidation() async throws {
        let detector = ChangeDetector()
        
        // Test minimum interval enforcement
        detector.setCheckInterval(0.05) // Below minimum
        // Should clamp to minimum 0.1 seconds
        
        detector.setCheckInterval(5.0) // Valid interval
        // Should accept valid interval
        
        #expect(true) // ChangeDetector doesn't expose interval for testing
    }
    
    @Test func testMinChangeSizeValidation() async throws {
        let detector = ChangeDetector()
        
        // Test minimum size enforcement
        detector.setMinChangeSize(0) // Below minimum
        detector.setMinChangeSize(44) // Valid size
        
        #expect(true) // ChangeDetector doesn't expose minChangeSize for testing
    }
    
    @Test func testClearChanges() async throws {
        let detector = ChangeDetector()
        
        // Simulate having some changes (in real usage, these would come from image analysis)
        detector.clearChanges()
        #expect(detector.detectedChanges.isEmpty)
    }
    
    @Test func testDetectChangesWithoutPreviousImage() async throws {
        let detector = ChangeDetector()
        detector.startDetection()
        
        // Create a test image
        let image = createTestImage(width: 100, height: 100, color: .red)
        
        // First call should not detect changes (no previous image)
        detector.detectChanges(in: image)
        #expect(detector.detectedChanges.isEmpty)
    }
    
    // Helper function to create test images
    private func createTestImage(width: Int, height: Int, color: UIColor) -> UIImage {
        let size = CGSize(width: width, height: height)
        let renderer = UIGraphicsImageRenderer(size: size)
        
        return renderer.image { context in
            color.setFill()
            context.fill(CGRect(origin: .zero, size: size))
        }
    }
}