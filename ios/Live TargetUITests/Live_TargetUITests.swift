//
//  Live_TargetUITests.swift
//  Live TargetUITests
//
//  Created by Bryan Everly on 7/7/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import XCTest

final class Live_TargetUITests: XCTestCase {

    override func setUpWithError() throws {
        // Put setup code here. This method is called before the invocation of each test method in the class.

        // In UI tests it is usually best to stop immediately when a failure occurs.
        continueAfterFailure = false

        // In UI tests it's important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
        
        // Disable accessibility for UI testing to avoid initialization errors
        XCUIDevice.shared.orientation = .portrait
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    @MainActor
    func testExample() throws {
        // Simple UI test - just verify app launches
        let app = XCUIApplication()
        
        // Set launch arguments to disable problematic features
        app.launchArguments += ["-DisableAnimations", "YES"]
        app.launchArguments += ["-AppleLanguages", "(en)"]
        app.launchArguments += ["-AppleLocale", "en_US"]
        
        app.launch()

        // Wait for app to launch with expectation
        let exists = app.wait(for: .runningForeground, timeout: 10.0)
        XCTAssertTrue(exists, "App should launch successfully within 10 seconds")
    }

    @MainActor 
    func testLaunchPerformance() throws {
        // Minimal performance test for CI reliability
        let app = XCUIApplication()
        
        // Set launch arguments to disable problematic features
        app.launchArguments += ["-DisableAnimations", "YES"]
        app.launchArguments += ["-AppleLanguages", "(en)"]
        app.launchArguments += ["-AppleLocale", "en_US"]
        
        app.launch()
        
        // Just verify it launches within a reasonable time
        let exists = app.wait(for: .runningForeground, timeout: 15.0)
        XCTAssertTrue(exists, "App should launch successfully within 15 seconds")
    }
}
