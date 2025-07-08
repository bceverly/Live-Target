//
//  LaunchScreenManager.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct LaunchScreenManager: View {
    @State private var showingSplash = true
    
    var body: some View {
        ZStack {
            if showingSplash {
                SplashScreenView()
                    .transition(.opacity)
            } else {
                ContentView()
                    .transition(.opacity)
            }
        }
        .onAppear {
            // Show splash screen for 3 seconds
            DispatchQueue.main.asyncAfter(deadline: .now() + 3.0) {
                withAnimation(.easeInOut(duration: 1.0)) {
                    showingSplash = false
                }
            }
        }
    }
}

#Preview {
    LaunchScreenManager()
}
