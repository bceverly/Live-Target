//
//  ContentView.swift
//  Live Target
//
//  Created by Bryan Everly on 7/7/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI
import SwiftData

struct WatchStatusIcon: View {
    let status: WatchConnectionStatus
    let integrationEnabled: Bool
    
    var body: some View {
        ZStack {
            // Base watch icon
            Image(systemName: "applewatch")
                .foregroundColor(iconColor)
                .font(.system(size: 16))
            
            // Error overlay (circle with diagonal line) - only if integration is enabled
            if integrationEnabled && (status == .disconnected || status == .error) {
                Image(systemName: "circle.slash")
                    .foregroundColor(.red)
                    .font(.system(size: 20))
                    .background(Color.clear)
            }
        }
    }
    
    private var iconColor: Color {
        // If integration is disabled, always show grey
        guard integrationEnabled else {
            return .gray
        }
        
        switch status {
        case .connected:
            return .green
        case .disconnected, .error:
            return .red
        case .unknown:
            return .gray
        }
    }
}

struct ContentView: View {
    @State private var capturedImage: UIImage?
    @StateObject private var changeDetector = ChangeDetector()
    @StateObject private var watchConnectivity = WatchConnectivityManager.shared
    @State private var showingSettings = false
    @State private var showingHelp = false
    @AppStorage("circleColor") private var circleColorHex: String = "FF0000"
    @AppStorage("numberColor") private var numberColorHex: String = "FF0000"
    @AppStorage("checkInterval") private var checkInterval: Double = 2.0
    @AppStorage("selectedCaliberName") private var selectedCaliberName: String = ".22 Long Rifle"
    @AppStorage("zoomFactor") private var zoomFactor: Double = 1.0
    @AppStorage("watchIntegrationEnabled") private var watchIntegrationEnabled: Bool = false
    
    // Overlay Settings
    @AppStorage("overlayEnabled") private var overlayEnabled: Bool = false
    @AppStorage("overlayPosition") private var overlayPositionRaw: String = OverlayPosition.topLeft.rawValue
    @AppStorage("bulletWeight") private var bulletWeight: Double = 55.0
    @AppStorage("ammoType") private var ammoTypeRaw: String = AmmoType.factory.rawValue
    @AppStorage("factoryAmmoName") private var factoryAmmoName: String = ""
    @AppStorage("handloadPowder") private var handloadPowder: String = ""
    @AppStorage("handloadCharge") private var handloadCharge: Double = 0.0
    
    private var caliberData = CaliberData.shared
    
    private var selectedCaliber: Caliber {
        return caliberData.findCaliber(byName: selectedCaliberName) ?? caliberData.calibers.first { $0.name == ".22 Long Rifle" }!
    }
    
    private var overlaySettings: OverlaySettings {
        return OverlaySettings(
            enabled: overlayEnabled,
            position: OverlayPosition(rawValue: overlayPositionRaw) ?? .topLeft,
            bulletWeight: bulletWeight,
            ammoType: AmmoType(rawValue: ammoTypeRaw) ?? .factory,
            factoryAmmoName: factoryAmmoName,
            handloadPowder: handloadPowder,
            handloadCharge: handloadCharge,
            selectedCaliberName: selectedCaliberName
        )
    }
    
    var body: some View {
        NavigationView {
            GeometryReader { geometry in
                ZStack {
                    CameraView(capturedImage: $capturedImage, zoomFactor: Binding(
                        get: { CGFloat(zoomFactor) },
                        set: { zoomFactor = Double($0) }
                    ))
                        .onChange(of: capturedImage) { _, newImage in
                            if let image = newImage {
                                changeDetector.detectChanges(in: image)
                            }
                        }
                        .onChange(of: checkInterval) { _, newInterval in
                            changeDetector.setCheckInterval(newInterval)
                        }
                        .onChange(of: selectedCaliberName) { _, _ in
                            changeDetector.setMinChangeSize(selectedCaliber.pixelSize)
                        }
                    
                    ForEach(changeDetector.detectedChanges, id: \.id) { change in
                        let screenPoint = CGPoint(
                            x: change.location.x * geometry.size.width,
                            y: change.location.y * geometry.size.height
                        )
                        
                        ZStack {
                            Circle()
                                .stroke(Color(hex: circleColorHex) ?? .red, lineWidth: 3)
                                .frame(width: 60, height: 60)
                            
                            Text("\(change.number)")
                                .font(.headline)
                                .fontWeight(.bold)
                                .foregroundColor(Color(hex: numberColorHex) ?? .red)
                        }
                        .position(screenPoint)
                    }
                    
                    // Zoom control positioned at the bottom
                    VStack {
                        Spacer()
                        ZoomControl(zoomFactor: Binding(
                            get: { CGFloat(zoomFactor) },
                            set: { zoomFactor = Double($0) }
                        ))
                        .padding(.bottom, 100) // Space for bottom toolbar
                    }
                }
            }
            .navigationTitle("Live Target")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarLeading) {
                    Button("Help") {
                        showingHelp = true
                    }
                }
                
                ToolbarItem(placement: .navigationBarTrailing) {
                    HStack {
                        // Watch connectivity status
                        WatchStatusIcon(status: watchConnectivity.watchConnectionStatus, integrationEnabled: watchIntegrationEnabled)
                        
                        Button("Settings") {
                            showingSettings = true
                        }
                    }
                }
                
                ToolbarItemGroup(placement: .bottomBar) {
                    Button("Clear") {
                        changeDetector.clearChanges()
                    }
                    .foregroundColor(.primary)
                    
                    Spacer()
                    
                    if changeDetector.isDetecting {
                        // When detecting: show Stop and Save buttons
                        Button("Stop") {
                            changeDetector.stopDetection()
                        }
                        .foregroundColor(.red)
                        .fontWeight(.semibold)
                        
                        Spacer()
                        
                        Button("Save") {
                            if let image = capturedImage {
                                let circleColor = UIColor(Color(hex: circleColorHex) ?? .red)
                                let numberColor = UIColor(Color(hex: numberColorHex) ?? .red)
                                changeDetector.saveCurrentImage(image, circleColor: circleColor, numberColor: numberColor, overlaySettings: overlaySettings)
                            }
                        }
                        .foregroundColor(.blue)
                        .fontWeight(.semibold)
                    } else {
                        // When stopped: show Start and Save buttons
                        Button("Start") {
                            // Always test watch connectivity when starting (for icon status)
                            watchConnectivity.testWatchConnectivity()
                            changeDetector.startDetection()
                        }
                        .foregroundColor(.green)
                        .fontWeight(.semibold)
                        
                        Spacer()
                        
                        Button("Save") {
                            if let image = capturedImage {
                                let circleColor = UIColor(Color(hex: circleColorHex) ?? .red)
                                let numberColor = UIColor(Color(hex: numberColorHex) ?? .red)
                                changeDetector.saveCurrentImage(image, circleColor: circleColor, numberColor: numberColor, overlaySettings: overlaySettings)
                            }
                        }
                        .foregroundColor(.blue)
                        .fontWeight(.semibold)
                    }
                }
            }
            .sheet(isPresented: $showingSettings) {
                SettingsView()
            }
            .sheet(isPresented: $showingHelp) {
                HelpView()
            }
            .onAppear {
                changeDetector.setCheckInterval(checkInterval)
                changeDetector.setMinChangeSize(selectedCaliber.pixelSize)
                
                // Always test watch connectivity on app launch for icon status
                watchConnectivity.testWatchConnectivity()
            }
            .task {
                // Set smart default for watch integration if not set yet
                // Use .task to ensure this runs early and only once
                if UserDefaults.standard.object(forKey: "watchIntegrationEnabled") == nil {
                    // Wait a moment for watch connectivity to initialize
                    try? await Task.sleep(nanoseconds: 500_000_000) // 0.5 seconds
                    await MainActor.run {
                        watchIntegrationEnabled = watchConnectivity.isWatchPaired
                    }
                }
            }
        }
    }
}

#Preview {
    ContentView()
        .modelContainer(for: Item.self, inMemory: true)
}
