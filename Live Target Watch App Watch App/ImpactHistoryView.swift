//
//  ImpactHistoryView.swift
//  Live Target Watch App Watch App
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import SwiftUI

struct ImpactHistoryView: View {
    @ObservedObject var impactStore = ImpactStore.shared
    
    var body: some View {
        NavigationView {
            List {
                if impactStore.impacts.isEmpty {
                    Text("No impacts recorded")
                        .foregroundColor(.secondary)
                        .frame(maxWidth: .infinity, maxHeight: .infinity)
                } else {
                    ForEach(impactStore.impacts.reversed()) { impact in
                        NavigationLink(destination: FullScreenImpactView(impact: impact)) {
                            ImpactRowView(impact: impact)
                        }
                    }
                }
            }
            .navigationTitle("History")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .primaryAction) {
                    Button("Clear") {
                        impactStore.clearImpacts()
                    }
                    .font(.caption)
                }
            }
        }
    }
}

struct ImpactRowView: View {
    let impact: WatchImpact
    
    var body: some View {
        HStack {
            if let image = impact.zoomedImage {
                Image(uiImage: image)
                    .resizable()
                    .aspectRatio(contentMode: .fit)
                    .frame(width: 40, height: 40)
                    .cornerRadius(6)
            } else {
                RoundedRectangle(cornerRadius: 6)
                    .fill(Color.gray.opacity(0.3))
                    .frame(width: 40, height: 40)
            }
            
            VStack(alignment: .leading, spacing: 2) {
                Text("Impact #\(impact.number)")
                    .font(.headline)
                    .foregroundColor(.red)
                
                Text(impact.timestamp.formatted(date: .omitted, time: .shortened))
                    .font(.caption2)
                    .foregroundColor(.secondary)
            }
            
            Spacer()
        }
        .padding(.vertical, 2)
    }
}

struct FullScreenImpactView: View {
    let impact: WatchImpact
    
    var body: some View {
        ScrollView([.horizontal, .vertical]) {
            VStack(spacing: 10) {
                Text("Impact #\(impact.number)")
                    .font(.headline)
                    .foregroundColor(.red)
                
                if let image = impact.zoomedImage {
                    Image(uiImage: image)
                        .resizable()
                        .aspectRatio(contentMode: .fit)
                        .cornerRadius(8)
                        .scaleEffect(1.0)
                } else {
                    RoundedRectangle(cornerRadius: 8)
                        .fill(Color.gray.opacity(0.3))
                        .frame(height: 120)
                        .overlay(
                            Text("Image not available")
                                .font(.caption)
                                .foregroundColor(.gray)
                        )
                }
                
                Text(impact.timestamp.formatted(date: .abbreviated, time: .shortened))
                    .font(.caption)
                    .foregroundColor(.secondary)
            }
            .padding()
        }
        .navigationTitle("Impact #\(impact.number)")
        .navigationBarTitleDisplayMode(.inline)
    }
}

#Preview {
    ImpactHistoryView()
}
