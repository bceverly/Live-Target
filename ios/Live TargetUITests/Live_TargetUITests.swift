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

        // Wait for the app to load and verify main UI elements exist
        let exists = NSPredicate(format: "exists == true")
        
        // Wait for any main UI element to appear (splash screen or main view)
        let splashOrMain = app.staticTexts.matching(identifier: "Live Target").firstMatch
        expectation(for: exists, evaluatedWith: splashOrMain, handler: nil)
        waitForExpectations(timeout: 10, handler: nil)
        
        // Basic assertion that app launched successfully
        XCTAssertTrue(app.state == .runningForeground)
    }

    @MainActor
    func testLaunchPerformance() throws {
        // This measures how long it takes to launch your application.
        if #available(iOS 13.0, *) {
            // Only run performance tests on iOS 13+ to avoid CI issues
            measure(metrics: [XCTApplicationLaunchMetric()]) {
                let app = XCUIApplication()
                app.launch()
                app.terminate()
            }
        } else {
            // For older iOS versions, just verify app launches
            let app = XCUIApplication()
            app.launch()
            XCTAssertTrue(app.state == .runningForeground)
        }
    }
}
