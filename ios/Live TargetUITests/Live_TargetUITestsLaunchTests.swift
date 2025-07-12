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
        
        // Set launch arguments to disable problematic features
        app.launchArguments += ["-DisableAnimations", "YES"]
        app.launchArguments += ["-AppleLanguages", "(en)"]
        app.launchArguments += ["-AppleLocale", "en_US"]
        
        app.launch()

        // Wait for app to stabilize with expectation
        let exists = app.wait(for: .runningForeground, timeout: 10.0)
        XCTAssertTrue(exists, "App should launch successfully within 10 seconds")
    }
}
