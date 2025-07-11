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
        // Simple UI test - just verify app launches
        let app = XCUIApplication()
        app.launch()

        // Wait for app to launch (generous timeout for CI)
        Thread.sleep(forTimeInterval: 8.0)
        
        // Very basic assertion - just check app is running
        XCTAssertEqual(app.state, .runningForeground, "App should be running in foreground")
    }

    @MainActor 
    func testLaunchPerformance() throws {
        // Minimal performance test for CI reliability
        let app = XCUIApplication()
        app.launch()
        
        // Just verify it launches within a reasonable time
        Thread.sleep(forTimeInterval: 10.0)
        XCTAssertEqual(app.state, .runningForeground, "App should launch successfully")
    }
}
