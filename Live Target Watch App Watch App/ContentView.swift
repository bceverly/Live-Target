//
//  ContentView.swift
//  Live Target Watch App Watch App
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var watchConnectivity = WatchConnectivityManager()
    @StateObject private var impactStore = ImpactStore.shared
    @State private var isLoading = true
    
    var body: some View {
        if isLoading {
            LoadingView()
                .onAppear {
                    watchConnectivity.activateSession()
                    // Small delay to ensure proper initialization
                    DispatchQueue.main.asyncAfter(deadline: .now() + 0.5) {
                        isLoading = false
                    }
                }
        } else {
            TabView {
                NavigationView {
                    VStack {
                        if watchConnectivity.isPhoneConnected {
                            if let latestImpact = impactStore.latestImpact {
                                LatestImpactView(impact: latestImpact)
                            } else {
                                WaitingForImpactView()
                            }
                        } else {
                            DisconnectedView()
                        }
                    }
                    .navigationTitle("Latest")
                    .navigationBarTitleDisplayMode(.inline)
                }
                .tabItem {
                    Image(systemName: "target")
                    Text("Latest")
                }
                
                ImpactHistoryView()
                    .tabItem {
                        Image(systemName: "clock")
                        Text("History")
                    }
            }
        }
    }
}

struct LoadingView: View {
    var body: some View {
        VStack(spacing: 15) {
            Image(systemName: "target")
                .font(.system(size: 50))
                .foregroundColor(.red)
            
            Text("Live Target")
                .font(.title2)
                .fontWeight(.bold)
            
            Text("Loading...")
                .font(.caption)
                .foregroundColor(.secondary)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(Color.black)
    }
}

struct LatestImpactView: View {
    let impact: WatchImpact
    
    var body: some View {
        VStack(spacing: 8) {
            Text("Impact #\(impact.number)")
                .font(.headline)
                .foregroundColor(.red)
            
            if let image = impact.zoomedImage {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .cornerRadius(8)
                    .frame(minHeight: 120)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: 120)
                    .overlay(
                        Text("Loading...")
                            .font(.caption)
                            .foregroundColor(.gray)
                    )
            }
            
            Text(impact.timestamp.formatted(date: .omitted, time: .shortened))
                .font(.caption2)
                .foregroundColor(.secondary)
        }
        .padding(8)
    }
}

struct WaitingForImpactView: View {
    var body: some View {
        VStack(spacing: 15) {
            Image(systemName: "target")
                .font(.system(size: 40))
                .foregroundColor(.red)
            
            Text("Ready")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text("Waiting for impacts...")
                .font(.caption)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding()
    }
}

struct DisconnectedView: View {
    var body: some View {
        VStack(spacing: 15) {
            Image(systemName: "target")
                .font(.system(size: 40))
                .foregroundColor(.red)
            
            Text("Live Target")
                .font(.title2)
                .fontWeight(.semibold)
            
            Text("Open Live Target on your iPhone")
                .font(.caption)
                .foregroundColor(.secondary)
                .multilineTextAlignment(.center)
        }
        .padding()
    }
}

#Preview {
    ContentView()
}