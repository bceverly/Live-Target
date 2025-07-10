//
//  Live_Target_Watch_AppApp.swift
//  Live Target Watch App Watch App
//
//  Created by Bryan Everly on 7/8/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI
import WatchKit

@main
struct Live_Target_Watch_App_Watch_AppApp: App {
    @Environment(\.scenePhase) private var scenePhase
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
        .onChange(of: scenePhase) { newPhase in
            handleScenePhaseChange(newPhase)
        }
    }
    
    private func handleScenePhaseChange(_ phase: ScenePhase) {
        switch phase {
        case .background:
            // App entering background - optimize for battery
            WatchConnectivityManager.shared.appDidEnterBackground()
        case .active:
            // App becoming active - resume full functionality
            WatchConnectivityManager.shared.appWillEnterForeground()
        case .inactive:
            // App becoming inactive - prepare for background
            break
        @unknown default:
            break
        }
    }
}
