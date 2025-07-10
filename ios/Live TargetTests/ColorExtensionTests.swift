//
//  ColorExtensionTests.swift
//  Live TargetTests
//
//  Created by Bryan Everly on 7/8/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Testing
import SwiftUI
@testable import Live_Target

struct ColorExtensionTests {
    
    @Test func testColorFromValidHexString() async throws {
        // Test valid hex colors
        let redColor = Color(hex: "FF0000")
        #expect(redColor != nil)
        
        let greenColor = Color(hex: "00FF00")
        #expect(greenColor != nil)
        
        let blueColor = Color(hex: "0000FF")
        #expect(blueColor != nil)
        
        // Test with hash prefix
        let whiteColor = Color(hex: "#FFFFFF")
        #expect(whiteColor != nil)
    }
    
    @Test func testColorFromInvalidHexString() async throws {
        // Test invalid hex strings
        let invalidColor1 = Color(hex: "GGGGGG") // Invalid characters
        #expect(invalidColor1 == nil)
        
        let invalidColor2 = Color(hex: "FF") // Too short
        #expect(invalidColor2 == nil)
        
        let invalidColor3 = Color(hex: "FFFFFFF") // Too long
        #expect(invalidColor3 == nil)
        
        let invalidColor4 = Color(hex: "") // Empty string
        #expect(invalidColor4 == nil)
    }
    
    @Test func testColorToHexConversion() async throws {
        // Test known colors
        let red = Color.red
        let hexString = red.toHex()
        
        // Should return a valid 6-character hex string
        #expect(hexString.count == 6)
        #expect(hexString.allSatisfy { "0123456789ABCDEF".contains($0) })
    }
    
    @Test func testHexRoundTrip() async throws {
        // Test that converting to hex and back produces similar colors
        let originalHex = "FF0000"
        let color = Color(hex: originalHex)
        #expect(color != nil)
        
        let convertedHex = color!.toHex()
        let finalColor = Color(hex: convertedHex)
        #expect(finalColor != nil)
        
        // The hex strings should be similar (allowing for minor rounding differences)
        #expect(convertedHex.hasPrefix("FF"))
    }
    
    @Test func testHexStringCleaning() async throws {
        // Test that hex strings are properly cleaned
        let colorWithSpaces = Color(hex: " FF0000 ")
        #expect(colorWithSpaces != nil)
        
        let colorWithHash = Color(hex: "#FF0000")
        #expect(colorWithHash != nil)
        
        let colorWithHashAndSpaces = Color(hex: " #FF0000 ")
        #expect(colorWithHashAndSpaces != nil)
    }
}
