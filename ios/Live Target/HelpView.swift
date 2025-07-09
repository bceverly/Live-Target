//
//  HelpView.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct HelpView: View {
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 20) {
                    
                    // MARK: - What is Live Target
                    VStack(alignment: .leading, spacing: 12) {
                        Text("What is Live Target?")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        Text("Live Target is an intelligent bullet impact detection system that uses your device's camera and computer vision to automatically identify and track bullet holes on targets during shooting practice.")
                            .fixedSize(horizontal: false, vertical: true)
                        
                        Text("The app continuously monitors your target through the camera feed and alerts you when new impacts are detected, making it perfect for:")
                            .fixedSize(horizontal: false, vertical: true)
                        
                        VStack(alignment: .leading, spacing: 4) {
                            Text("• Target shooting practice")
                            Text("• Accuracy training")
                            Text("• Shot group analysis")
                            Text("• Remote target monitoring")
                        }
                        .padding(.leading, 16)
                    }
                    
                    Divider()
                    
                    // MARK: - How to Use
                    VStack(alignment: .leading, spacing: 12) {
                        Text("How to Use Live Target")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        VStack(alignment: .leading, spacing: 8) {
                            HStack(alignment: .top) {
                                Text("1.")
                                    .fontWeight(.medium)
                                    .frame(width: 20, alignment: .leading)
                                Text("Position your device so the target is clearly visible in the camera view")
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                            
                            HStack(alignment: .top) {
                                Text("2.")
                                    .fontWeight(.medium)
                                    .frame(width: 20, alignment: .leading)
                                Text("Adjust settings (caliber, colors, check frequency) as needed")
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                            
                            HStack(alignment: .top) {
                                Text("3.")
                                    .fontWeight(.medium)
                                    .frame(width: 20, alignment: .leading)
                                Text("Tap 'Start' to begin impact detection")
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                            
                            HStack(alignment: .top) {
                                Text("4.")
                                    .fontWeight(.medium)
                                    .frame(width: 20, alignment: .leading)
                                Text("Fire at your target - new impacts will be automatically detected and numbered")
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                            
                            HStack(alignment: .top) {
                                Text("5.")
                                    .fontWeight(.medium)
                                    .frame(width: 20, alignment: .leading)
                                Text("Use 'Save' to capture target images with impact annotations")
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                        }
                    }
                    
                    Divider()
                    
                    // MARK: - Settings Explained
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Settings Explained")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        VStack(alignment: .leading, spacing: 16) {
                            
                            // Colors Section
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Colors")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("• Circle Color: Color of the circles drawn around detected impacts")
                                    Text("• Number Color: Color of the impact numbers displayed on screen")
                                }
                                .font(.body)
                                .foregroundColor(.secondary)
                            }
                            
                            // Change Detection Section
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Change Detection")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("• Check Frequency: How often the app compares camera frames (0.5-10 seconds)")
                                        .fixedSize(horizontal: false, vertical: true)
                                    Text("• Bullet Caliber: Sets the minimum size for impact detection (17-70 caliber)")
                                        .fixedSize(horizontal: false, vertical: true)
                                    Text("  Higher caliber = larger minimum detection size")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                }
                                .font(.body)
                                .foregroundColor(.secondary)
                            }
                            
                            // Apple Watch Section
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Apple Watch")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("• Watch Integration: Enable/disable sending impact notifications to Apple Watch")
                                        .fixedSize(horizontal: false, vertical: true)
                                    Text("• Watch Status: Shows pairing and app installation status")
                                        .fixedSize(horizontal: false, vertical: true)
                                    Text("When enabled, your Apple Watch will receive haptic feedback and display impact details")
                                        .font(.caption)
                                        .foregroundColor(.secondary)
                                        .fixedSize(horizontal: false, vertical: true)
                                }
                                .font(.body)
                                .foregroundColor(.secondary)
                            }
                        }
                    }
                    
                    Divider()
                    
                    // MARK: - Controls & Icons
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Controls & Status Icons")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        VStack(alignment: .leading, spacing: 12) {
                            
                            // Main Controls
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Main Controls")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("• Start/Stop: Begin or end impact detection")
                                    Text("• Clear: Remove all detected impact markers from screen")
                                    Text("• Save: Capture current view with impact annotations to Photos")
                                    Text("• Settings: Access app configuration options")
                                }
                                .font(.body)
                                .foregroundColor(.secondary)
                            }
                            
                            // Watch Status Icons
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Watch Status Icons")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    HStack {
                                        Image(systemName: "applewatch")
                                            .foregroundColor(.green)
                                        Text("Green watch: Connected and ready")
                                    }
                                    
                                    HStack {
                                        ZStack {
                                            Image(systemName: "applewatch")
                                                .foregroundColor(.red)
                                            Image(systemName: "circle.slash")
                                                .foregroundColor(.red)
                                        }
                                        Text("Red watch with slash: Connection failed or unavailable")
                                    }
                                    
                                    HStack {
                                        Image(systemName: "applewatch")
                                            .foregroundColor(.gray)
                                        Text("Gray watch: Status unknown or no watch paired")
                                    }
                                }
                                .font(.body)
                                .foregroundColor(.secondary)
                            }
                            
                            // Zoom Controls
                            VStack(alignment: .leading, spacing: 6) {
                                Text("Zoom Controls")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                VStack(alignment: .leading, spacing: 4) {
                                    Text("• Use + and - buttons or slider to adjust camera zoom")
                                    Text("• Zoom range: 1x to 10x magnification")
                                    Text("• Higher zoom provides more detail but smaller field of view")
                                }
                                .font(.body)
                                .foregroundColor(.secondary)
                            }
                        }
                    }
                    
                    Divider()
                    
                    // MARK: - Tips for Best Results
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Tips for Best Results")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        VStack(alignment: .leading, spacing: 8) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Camera Positioning")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                Text("• Mount device securely to minimize camera shake")
                                Text("• Ensure target fills a good portion of the camera view")
                                Text("• Avoid backlighting or extreme shadows on target")
                                Text("• Position camera perpendicular to target face")
                            }
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Optimal Settings")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                Text("• Start with 2-second check frequency for most situations")
                                Text("• Set bullet caliber close to your actual ammunition")
                                Text("• Use contrasting colors for impact markers")
                                Text("• Lower check frequency in bright, stable lighting")
                            }
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Environment")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                Text("• Use consistent, even lighting on target")
                                Text("• Minimize movement around the target area")
                                Text("• Start detection before first shot for best baseline")
                                Text("• Clean target background improves detection accuracy")
                            }
                        }
                        .font(.body)
                        .foregroundColor(.secondary)
                    }
                    
                    Divider()
                    
                    // MARK: - Troubleshooting
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Troubleshooting")
                            .font(.title2)
                            .fontWeight(.semibold)
                        
                        VStack(alignment: .leading, spacing: 12) {
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Detection Issues")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                Text("• False detections: Increase bullet caliber setting or check frequency")
                                Text("• Missed impacts: Decrease bullet caliber setting or improve lighting")
                                Text("• Multiple detections per shot: Increase check frequency")
                            }
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Apple Watch Issues")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                Text("• Check that watch is paired and Live Target app is installed")
                                Text("• Ensure watch integration is enabled in settings")
                                Text("• Verify watch is connected (green watch icon)")
                                Text("• Try tapping Start button to test connectivity")
                            }
                            
                            VStack(alignment: .leading, spacing: 4) {
                                Text("Performance Issues")
                                    .font(.headline)
                                    .foregroundColor(.primary)
                                
                                Text("• Close other camera apps before using Live Target")
                                Text("• Restart app if detection becomes unresponsive")
                                Text("• Use lower zoom levels for better performance")
                                Text("• Increase check frequency if device is struggling")
                            }
                        }
                        .font(.body)
                        .foregroundColor(.secondary)
                    }
                }
                .padding()
            }
            .navigationTitle("Help")
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

#Preview {
    HelpView()
}