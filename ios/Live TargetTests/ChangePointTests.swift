//
//  ChangePointTests.swift
//  Live TargetTests
//
//  Created by Bryan Everly on 7/8/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Testing
import CoreGraphics
@testable import Live_Target

struct ChangePointTests {
    
    @Test func testChangePointCreation() async throws {
        let location = CGPoint(x: 0.5, y: 0.3)
        let number = 5
        
        let changePoint = ChangePoint(location: location, number: number)
        
        #expect(changePoint.location.x == 0.5)
        #expect(changePoint.location.y == 0.3)
        #expect(changePoint.number == 5)
        // UUID is always generated, so just verify it exists by checking it's not empty
        #expect(!changePoint.id.uuidString.isEmpty)
    }
    
    @Test func testChangePointUniqueIDs() async throws {
        let changePoint1 = ChangePoint(location: CGPoint(x: 0.1, y: 0.1), number: 1)
        let changePoint2 = ChangePoint(location: CGPoint(x: 0.1, y: 0.1), number: 1)
        
        // Even with identical data, IDs should be unique
        #expect(changePoint1.id != changePoint2.id)
    }
    
    @Test func testChangePointBoundaryValues() async throws {
        // Test with boundary values for normalized coordinates
        let topLeft = ChangePoint(location: CGPoint(x: 0.0, y: 0.0), number: 1)
        let bottomRight = ChangePoint(location: CGPoint(x: 1.0, y: 1.0), number: 2)
        let center = ChangePoint(location: CGPoint(x: 0.5, y: 0.5), number: 3)
        
        #expect(topLeft.location.x == 0.0)
        #expect(topLeft.location.y == 0.0)
        #expect(bottomRight.location.x == 1.0)
        #expect(bottomRight.location.y == 1.0)
        #expect(center.location.x == 0.5)
        #expect(center.location.y == 0.5)
    }
    
    @Test func testChangePointWithNegativeNumbers() async throws {
        // Test that negative numbers are handled (shouldn't normally happen, but test robustness)
        let changePoint = ChangePoint(location: CGPoint(x: 0.5, y: 0.5), number: -1)
        #expect(changePoint.number == -1)
    }
    
    @Test func testChangePointWithLargeNumbers() async throws {
        // Test with large impact numbers
        let changePoint = ChangePoint(location: CGPoint(x: 0.5, y: 0.5), number: 999999)
        #expect(changePoint.number == 999999)
    }
    
    @Test func testChangePointWithOutOfBoundsCoordinates() async throws {
        // Test that the struct accepts out-of-bounds coordinates (validation should happen elsewhere)
        let outOfBounds = ChangePoint(location: CGPoint(x: -0.5, y: 1.5), number: 1)
        #expect(outOfBounds.location.x == -0.5)
        #expect(outOfBounds.location.y == 1.5)
    }
}
