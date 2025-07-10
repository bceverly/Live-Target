//
//  ContentView.swift
//  Live Target Watch App
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct ContentView: View {
    @StateObject private var watchConnectivity = WatchConnectivityManager()
    @StateObject private var impactStore = ImpactStore()
    
    var body: some View {
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
        .onAppear {
            watchConnectivity.activateSession()
        }
    }
}

struct LatestImpactView: View {
    let impact: WatchImpact
    
    var body: some View {
        VStack(spacing: 10) {
            Text("Impact #\(impact.number)")
                .font(.headline)
                .foregroundColor(.red)
            
            if let image = impact.zoomedImage {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .cornerRadius(8)
                    .frame(maxWidth: .infinity, maxHeight: .infinity)
            } else {
                RoundedRectangle(cornerRadius: 8)
                    .fill(Color.gray.opacity(0.3))
                    .frame(height: 100)
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
        .padding()
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
            Image(systemName: "iphone.slash")
                .font(.system(size: 40))
                .foregroundColor(.gray)
            
            Text("Disconnected")
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
