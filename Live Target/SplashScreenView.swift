//
//  SplashScreenView.swift
//  Live Target
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct SplashScreenView: View {
    var body: some View {
        ZStack {
            // Background
            Color.black
                .ignoresSafeArea()
            
            VStack {
                Spacer()
                
                // Target with bullet holes and circles
                ZStack {
                    // Target background (larger)
                    TargetView()
                        .frame(width: 400, height: 400)
                    
                    // Bullet holes with red circles positioned centrally on target
                    BulletHoleView(position: CGPoint(x: 200, y: 180), number: 1)  // Close to bullseye
                    BulletHoleView(position: CGPoint(x: 230, y: 200), number: 2)  // Inner ring, right
                    BulletHoleView(position: CGPoint(x: 170, y: 200), number: 3)  // Inner ring, left
                }
                
                Spacer()
                
                // App title
                Text("Live Target")
                    .font(.largeTitle)
                    .fontWeight(.bold)
                    .foregroundColor(.white)
                    .padding(.bottom, 20)
                
                // Loading indicator
                ProgressView()
                    .progressViewStyle(CircularProgressViewStyle(tint: .white))
                    .scaleEffect(1.5)
                    .padding(.bottom, 20)
                
                // Copyright notice
                Text("Copyright © 2025 BCEAssociates, Inc.")
                    .font(.caption)
                    .foregroundColor(.gray)
                
                Spacer()
            }
        }
    }
}

struct TargetView: View {
    var body: some View {
        ZStack {
            // Outer rings (alternating white and black) - larger rings
            ForEach(0..<6) { ring in
                Circle()
                    .fill(ring % 2 == 0 ? Color.white : Color.black)
                    .frame(width: CGFloat(400 - ring * 40), height: CGFloat(400 - ring * 40))
            }
            
            // Center bullseye (larger)
            Circle()
                .fill(Color.red)
                .frame(width: 80, height: 80)
            
            Circle()
                .fill(Color.white)
                .frame(width: 40, height: 40)
        }
    }
}

struct BulletHoleView: View {
    let position: CGPoint
    let number: Int
    
    var body: some View {
        ZStack {
            // Red circle around bullet hole
            Circle()
                .stroke(Color.red, lineWidth: 3)
                .frame(width: 60, height: 60)
            
            // Bullet hole
            Circle()
                .fill(Color.black)
                .frame(width: 8, height: 8)
            
            // Number
            Text("\(number)")
                .font(.headline)
                .fontWeight(.bold)
                .foregroundColor(.red)
                .offset(x: 25, y: -25)
        }
        .position(position)
    }
}

#Preview {
    SplashScreenView()
}