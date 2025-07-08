//
//  ImpactStore.swift
//  Live Target Watch App Watch App
//
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Foundation
import UIKit

struct WatchImpact: Identifiable {
    let id = UUID()
    let number: Int
    let timestamp: Date
    let zoomedImage: UIImage?
}

class ImpactStore: ObservableObject {
    static let shared = ImpactStore()
    
    @Published var impacts: [WatchImpact] = []
    @Published var latestImpact: WatchImpact?
    
    init() {}
    
    func addImpact(_ impact: WatchImpact) {
        impacts.append(impact)
        latestImpact = impact
        
        // Keep only last 10 impacts to manage memory
        if impacts.count > 10 {
            impacts.removeFirst()
        }
    }
    
    func clearImpacts() {
        impacts.removeAll()
        latestImpact = nil
    }
}
