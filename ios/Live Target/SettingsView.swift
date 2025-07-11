//
//  SettingsView.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct SettingsView: View {
    @AppStorage("circleColor") private var circleColorHex: String = "FF0000"
    @AppStorage("numberColor") private var numberColorHex: String = "FF0000"
    @AppStorage("checkInterval") private var checkInterval: Double = 2.0
    @AppStorage("selectedCaliberName") private var selectedCaliberName: String = ".22 Long Rifle"
    @AppStorage("watchIntegrationEnabled") private var watchIntegrationEnabled: Bool = false
    @StateObject private var watchConnectivity = WatchConnectivityManager.shared
    @Environment(\.dismiss) private var dismiss
    
    private var caliberData = CaliberData.shared
    
    private var selectedCaliber: Caliber {
        return caliberData.findCaliber(byName: selectedCaliberName) ?? caliberData.calibers.first { $0.name == ".22 Long Rifle" }!
    }
    
    // MARK: - App Information
    private var appVersion: String {
        if let version = Bundle.main.object(forInfoDictionaryKey: "CFBundleShortVersionString") as? String,
           let build = Bundle.main.object(forInfoDictionaryKey: "CFBundleVersion") as? String {
            return "\(version) (\(build))"
        }
        return "0.9"
    }
    
    private var buildDate: String {
        // Try to get build date from bundle info first
        if let buildDateString = Bundle.main.object(forInfoDictionaryKey: "BuildDate") as? String {
            return buildDateString
        }
        
        // Use a compile-time constant for the build date
        // In a real CI/CD environment, this would be injected during build
        return "January 8, 2025"
    }
    
    var body: some View {
        NavigationView {
            Form {
                Section(header: Text("Colors")) {
                    ColorPicker("Circle Color", selection: Binding(
                        get: { Color(hex: circleColorHex) ?? .red },
                        set: { circleColorHex = $0.toHex() }
                    ))
                    
                    ColorPicker("Number Color", selection: Binding(
                        get: { Color(hex: numberColorHex) ?? .red },
                        set: { numberColorHex = $0.toHex() }
                    ))
                }
                
                Section(header: Text("Change Detection")) {
                    VStack(alignment: .leading) {
                        Text("Check Frequency")
                        HStack {
                            Slider(value: $checkInterval, in: 0.5...10.0, step: 0.5)
                            Text("\(checkInterval, specifier: "%.1f")s")
                                .frame(width: 40)
                        }
                    }
                    
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Bullet Caliber")
                        
                        Menu {
                            Section("Common Calibers") {
                                ForEach(caliberData.commonCalibers) { caliber in
                                    Button(caliber.name) {
                                        selectedCaliberName = caliber.name
                                    }
                                }
                            }
                            
                            Section("Rimfire") {
                                ForEach(caliberData.rimfireCalibers) { caliber in
                                    Button(caliber.name) {
                                        selectedCaliberName = caliber.name
                                    }
                                }
                            }
                            
                            Section("Pistol Calibers") {
                                ForEach(caliberData.pistolCalibers) { caliber in
                                    Button(caliber.name) {
                                        selectedCaliberName = caliber.name
                                    }
                                }
                            }
                            
                            Section("Rifle Calibers") {
                                ForEach(caliberData.rifleCalibers) { caliber in
                                    Button(caliber.name) {
                                        selectedCaliberName = caliber.name
                                    }
                                }
                            }
                        } label: {
                            HStack {
                                Text(selectedCaliber.name)
                                    .foregroundColor(.primary)
                                Spacer()
                                Image(systemName: "chevron.down")
                                    .foregroundColor(.secondary)
                                    .font(.caption)
                            }
                            .padding(.vertical, 8)
                            .padding(.horizontal, 12)
                            .background(Color(.systemGray6))
                            .cornerRadius(8)
                        }
                        
                        HStack {
                            Text("Diameter: \(selectedCaliber.diameterInches, specifier: "%.3f")\"")
                                .font(.caption)
                                .foregroundColor(.secondary)
                            Spacer()
                            Text("(\(selectedCaliber.pixelSize)×\(selectedCaliber.pixelSize) pixels)")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
                }
                
                Section(header: Text("Apple Watch")) {
                    VStack(alignment: .leading, spacing: 8) {
                        Toggle("Watch Integration", isOn: $watchIntegrationEnabled)
                            .onChange(of: watchIntegrationEnabled) { _, newValue in
                                if newValue {
                                    // Test connectivity when enabling integration
                                    watchConnectivity.testWatchConnectivity()
                                }
                            }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            HStack {
                                Text("Watch Status:")
                                    .foregroundColor(.secondary)
                                Spacer()
                                if watchConnectivity.isWatchPaired {
                                    if watchConnectivity.isWatchAppInstalled {
                                        Text("App Installed")
                                            .foregroundColor(.green)
                                    } else {
                                        Text("Paired, No App")
                                            .foregroundColor(.orange)
                                    }
                                } else {
                                    Text("No Watch Paired")
                                        .foregroundColor(.secondary)
                                }
                            }
                            
                            if watchConnectivity.isWatchPaired && !watchConnectivity.isWatchAppInstalled {
                                Text("Install the Live Target app on your Apple Watch to receive impact notifications.")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .fixedSize(horizontal: false, vertical: true)
                            } else if !watchConnectivity.isWatchPaired {
                                Text("Pair an Apple Watch to enable watch integration features.")
                                    .font(.caption)
                                    .foregroundColor(.secondary)
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                        }
                    }
                }
                
                Section(header: Text("About")) {
                    VStack(alignment: .leading, spacing: 12) {
                        HStack {
                            Text("App Name")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text("Live Target")
                                .fontWeight(.medium)
                        }
                        
                        HStack {
                            Text("Version")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(appVersion)
                                .fontWeight(.medium)
                        }
                        
                        HStack {
                            Text("Build Date")
                                .foregroundColor(.secondary)
                            Spacer()
                            Text(buildDate)
                                .fontWeight(.medium)
                        }
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Copyright")
                                .foregroundColor(.secondary)
                            Text("© 2025 BCEAssociates, Inc.\nAll rights reserved.")
                                .font(.caption)
                                .foregroundColor(.secondary)
                                .multilineTextAlignment(.leading)
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }
                    .padding(.vertical, 8)
                }
            }
            .navigationTitle("Settings")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Done") {
                        dismiss()
                    }
                }
            }
            .onAppear {
                // Set smart default for watch integration based on pairing status
                // Only set default if this is the first time opening settings
                if UserDefaults.standard.object(forKey: "watchIntegrationEnabled") == nil {
                    watchIntegrationEnabled = watchConnectivity.isWatchPaired
                }
            }
        }
    }
}

extension Color {
    init?(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        
        // Validate hex string is exactly 6 characters
        guard hexSanitized.count == 6 else { return nil }
        
        // Validate all characters are valid hex digits
        guard hexSanitized.allSatisfy({ "0123456789ABCDEFabcdef".contains($0) }) else { return nil }
        
        var rgb: UInt64 = 0
        
        guard Scanner(string: hexSanitized).scanHexInt64(&rgb) else { return nil }
        
        self.init(
            red: Double((rgb & 0xFF0000) >> 16) / 255.0,
            green: Double((rgb & 0x00FF00) >> 8) / 255.0,
            blue: Double(rgb & 0x0000FF) / 255.0
        )
    }
    
    func toHex() -> String {
        let uic = UIColor(self)
        guard let components = uic.cgColor.components, components.count >= 3 else {
            return "FF0000"
        }
        let red = Float(components[0])
        let green = Float(components[1])
        let blue = Float(components[2])
        
        return String(format: "%02lX%02lX%02lX", lroundf(red * 255), lroundf(green * 255), lroundf(blue * 255))
    }
}

#Preview {
    SettingsView()
}
