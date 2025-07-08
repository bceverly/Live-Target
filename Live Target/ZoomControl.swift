//
//  ZoomControl.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct ZoomControl: View {
    @Binding var zoomFactor: CGFloat
    @State private var showingZoomSlider = false
    
    let zoomLevels: [CGFloat] = [1.0, 1.5, 2.0, 3.0, 5.0, 7.0, 10.0]
    
    var body: some View {
        VStack(spacing: 0) {
            if showingZoomSlider {
                ZoomSlider(zoomFactor: $zoomFactor)
                    .transition(.opacity.combined(with: .move(edge: .bottom)))
                    .animation(.easeInOut(duration: 0.3), value: showingZoomSlider)
            }
            
            HStack(spacing: 10) {
                ForEach(zoomLevels, id: \.self) { level in
                    ZoomButton(
                        level: level,
                        currentZoom: zoomFactor,
                        action: { 
                            withAnimation(.easeInOut(duration: 0.3)) {
                                zoomFactor = level
                            }
                        }
                    )
                }
                
                // Toggle slider button
                Button(action: {
                    withAnimation(.easeInOut(duration: 0.3)) {
                        showingZoomSlider.toggle()
                    }
                }) {
                    Image(systemName: showingZoomSlider ? "slider.horizontal.3" : "plus.magnifyingglass")
                        .font(.system(size: 16, weight: .medium))
                        .foregroundColor(.white)
                        .frame(width: 40, height: 40)
                        .background(
                            Circle()
                                .fill(Color.black.opacity(0.6))
                                .overlay(
                                    Circle()
                                        .stroke(Color.white.opacity(0.3), lineWidth: 1)
                                )
                        )
                }
            }
            .padding(.horizontal, 20)
            .padding(.vertical, 10)
        }
    }
}

struct ZoomButton: View {
    let level: CGFloat
    let currentZoom: CGFloat
    let action: () -> Void
    
    var isSelected: Bool {
        abs(currentZoom - level) < 0.1
    }
    
    var body: some View {
        Button(action: action) {
            Text(level == 1.0 ? "1×" : String(format: "%.1f×", level))
                .font(.system(size: 14, weight: .medium))
                .foregroundColor(isSelected ? .black : .white)
                .frame(width: 40, height: 40)
                .background(
                    Circle()
                        .fill(isSelected ? Color.yellow : Color.black.opacity(0.6))
                        .overlay(
                            Circle()
                                .stroke(Color.white.opacity(0.3), lineWidth: 1)
                        )
                )
        }
        .scaleEffect(isSelected ? 1.1 : 1.0)
        .animation(.easeInOut(duration: 0.2), value: isSelected)
    }
}

struct ZoomSlider: View {
    @Binding var zoomFactor: CGFloat
    
    var body: some View {
        VStack(spacing: 8) {
            HStack {
                Text("1×")
                    .font(.caption)
                    .foregroundColor(.white)
                
                Slider(value: $zoomFactor, in: 1.0...10.0, step: 0.1)
                    .accentColor(.yellow)
                
                Text("10×")
                    .font(.caption)
                    .foregroundColor(.white)
            }
            .padding(.horizontal, 20)
            
            Text(String(format: "%.1f×", zoomFactor))
                .font(.caption)
                .foregroundColor(.white)
                .padding(.horizontal, 8)
                .padding(.vertical, 4)
                .background(
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color.black.opacity(0.6))
                )
        }
        .padding(.vertical, 10)
        .background(
            RoundedRectangle(cornerRadius: 12)
                .fill(Color.black.opacity(0.3))
        )
        .padding(.horizontal, 20)
    }
}

#Preview {
    ZoomControl(zoomFactor: .constant(2.0))
        .background(Color.gray)
}