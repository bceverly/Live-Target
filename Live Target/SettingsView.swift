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
    @AppStorage("bulletCaliber") private var bulletCaliber: Int = 22
    @Environment(\.dismiss) private var dismiss
    
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
                    
                    VStack(alignment: .leading) {
                        Text("Bullet Caliber")
                        HStack {
                            Stepper(value: $bulletCaliber, in: 17...70, step: 1) {
                                Text("\(bulletCaliber)")
                            }
                            Text("(\(bulletCaliber * 2)×\(bulletCaliber * 2) pixels)")
                                .font(.caption)
                                .foregroundColor(.secondary)
                        }
                    }
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
        }
    }
}

extension Color {
    init?(hex: String) {
        var hexSanitized = hex.trimmingCharacters(in: .whitespacesAndNewlines)
        hexSanitized = hexSanitized.replacingOccurrences(of: "#", with: "")
        
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
