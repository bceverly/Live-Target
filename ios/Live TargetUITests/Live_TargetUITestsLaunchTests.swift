//
//  Live_TargetUITestsLaunchTests.swift
//  Live TargetUITests
//
//  Created by Bryan Everly on 7/7/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import XCTest

final class Live_TargetUITestsLaunchTests: XCTestCase {

    override class var runsForEachTargetApplicationUIConfiguration: Bool {
        true
    }

    override func setUpWithError() throws {
        continueAfterFailure = false
    }

    @MainActor
    func testLaunch() throws {
        let app = XCUIApplication()
        app.launch()

        // Wait for app to stabilize (CI-friendly timeout)
        Thread.sleep(forTimeInterval: 5.0)
        
        // Just verify app launched successfully without screenshot
        XCTAssertEqual(app.state, .runningForeground, "App should launch successfully")
    }
}
