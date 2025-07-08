//
//  ContentView.swift
//  Live Target
//
//  Created by Bryan Everly on 7/7/25.
//

import SwiftUI
import SwiftData

struct ContentView: View {
    @State private var capturedImage: UIImage?
    @StateObject private var changeDetector = ChangeDetector()
    @State private var showingSettings = false
    @AppStorage("circleColor") private var circleColorHex: String = "FF0000"
    @AppStorage("numberColor") private var numberColorHex: String = "FF0000"
    @AppStorage("checkInterval") private var checkInterval: Double = 2.0
    @AppStorage("bulletCaliber") private var bulletCaliber: Int = 22
    
    var body: some View {
        NavigationView {
            GeometryReader { geometry in
                ZStack {
                    CameraView(capturedImage: $capturedImage)
                        .onChange(of: capturedImage) { _, newImage in
                            if let image = newImage {
                                changeDetector.detectChanges(in: image)
                            }
                        }
                        .onChange(of: checkInterval) { _, newInterval in
                            changeDetector.setCheckInterval(newInterval)
                        }
                        .onChange(of: bulletCaliber) { _, newCaliber in
                            changeDetector.setMinChangeSize(newCaliber * 2)
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
                }
            }
            .navigationTitle("Live Target")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .navigationBarTrailing) {
                    Button("Settings") {
                        showingSettings = true
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
                                changeDetector.saveCurrentImage(image, circleColor: circleColor, numberColor: numberColor)
                            }
                        }
                        .foregroundColor(.blue)
                        .fontWeight(.semibold)
                    } else {
                        // When stopped: show Start and Save buttons
                        Button("Start") {
                            changeDetector.startDetection()
                        }
                        .foregroundColor(.green)
                        .fontWeight(.semibold)
                        
                        Spacer()
                        
                        Button("Save") {
                            if let image = capturedImage {
                                let circleColor = UIColor(Color(hex: circleColorHex) ?? .red)
                                let numberColor = UIColor(Color(hex: numberColorHex) ?? .red)
                                changeDetector.saveCurrentImage(image, circleColor: circleColor, numberColor: numberColor)
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
            .onAppear {
                changeDetector.setCheckInterval(checkInterval)
                changeDetector.setMinChangeSize(bulletCaliber * 2)
            }
        }
    }
}


#Preview {
    ContentView()
        .modelContainer(for: Item.self, inMemory: true)
}
