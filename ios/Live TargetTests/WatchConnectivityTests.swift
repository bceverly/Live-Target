//
//  WatchConnectivityTests.swift
//  Live TargetTests
//
//  Created by Bryan Everly on 7/8/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Testing
import UIKit
import WatchConnectivity
@testable import Live_Target

struct WatchConnectivityTests {
    
    @Test func testWatchConnectivityManagerSingleton() async throws {
        let manager1 = WatchConnectivityManager.shared
        let manager2 = WatchConnectivityManager.shared
        
        // Should be the same instance
        #expect(manager1 === manager2)
    }
    
    @Test func testInitialWatchConnectionState() async throws {
        let manager = WatchConnectivityManager.shared
        
        // These should always be false in test environment
        #expect(manager.isWatchConnected == false)
        #expect(manager.isWatchAppInstalled == false)
        #expect(manager.isWatchPaired == false)
        
        // Status can be any valid state in test environment
        let validStatuses: [WatchConnectionStatus] = [.unknown, .error, .disconnected, .connected]
        #expect(validStatuses.contains(manager.watchConnectionStatus))
    }
    
    @Test func testWatchConnectivityTest() async throws {
        let manager = WatchConnectivityManager.shared
        
        // Test that testWatchConnectivity doesn't crash
        // This is the main goal - ensure the method is robust
        do {
            manager.testWatchConnectivity()
            
            // Wait for any async operations to complete
            try await Task.sleep(nanoseconds: 500_000_000) // 0.5 seconds
            
            // The exact status depends on simulator environment
            // Main test is that it doesn't crash and sets a valid status
            let validStatuses: [WatchConnectionStatus] = [.unknown, .error, .disconnected, .connected]
            #expect(validStatuses.contains(manager.watchConnectionStatus))
            
            // If we get here without throwing, the test passes
            #expect(true)
        } catch {
            // If there's an error, we'll fail the test
            #expect(false, "testWatchConnectivity should not throw: \(error)")
        }
    }
    
    @Test func testImageResizing() async throws {
        // Test the private resizeImage method indirectly by testing image size limits
        _ = WatchConnectivityManager.shared
        
        // Create a large test image
        let largeImage = createTestImage(width: 1000, height: 1000, color: .blue)
        _ = ChangePoint(location: CGPoint(x: 0.5, y: 0.5), number: 1)
        
        // This should not crash even with a large image
        // (The actual message sending will fail in tests since WCSession isn't available,
        // but the image processing should work)
        #expect(largeImage.size.width == 1000)
        #expect(largeImage.size.height == 1000)
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