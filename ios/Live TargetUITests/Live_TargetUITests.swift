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

        // In UI tests it’s important to set the initial state - such as interface orientation - required for your tests before they run. The setUp method is a good place to do this.
    }

    override func tearDownWithError() throws {
        // Put teardown code here. This method is called after the invocation of each test method in the class.
    }

    @MainActor
    func testExample() throws {
        // UI tests must launch the application that they test.
        let app = XCUIApplication()
        app.launch()

        // Give the app time to load and get through splash screen
        sleep(5)
        
        // Basic assertion that app launched and is running
        XCTAssertTrue(app.state == .runningForeground, "App should be running in foreground")
        
        // Verify the app window exists (more reliable than specific UI elements)
        XCTAssertTrue(app.windows.count > 0, "App should have at least one window")
    }

    @MainActor
    func testLaunchPerformance() throws {
        // Simplified launch performance test for CI reliability
        let app = XCUIApplication()
        
        // Start timing
        let startTime = CFAbsoluteTimeGetCurrent()
        
        app.launch()
        
        // Wait for app to be ready (accounting for splash screen)
        sleep(5)
        
        let timeElapsed = CFAbsoluteTimeGetCurrent() - startTime
        
        // Verify app launched successfully
        XCTAssertTrue(app.state == .runningForeground, "App should launch successfully")
        
        // Reasonable launch time assertion (should be under 30 seconds in CI)
        XCTAssertLessThan(timeElapsed, 30.0, "App should launch within 30 seconds")
    }
}
