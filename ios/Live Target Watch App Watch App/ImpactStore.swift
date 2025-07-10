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
    private let _zoomedImage: UIImage?
    
    // Lazy loading for battery optimization
    var zoomedImage: UIImage? {
        return _zoomedImage?.resizedForWatch()
    }
    
    init(number: Int, timestamp: Date, zoomedImage: UIImage?) {
        self.number = number
        self.timestamp = timestamp
        self._zoomedImage = zoomedImage
    }
}

class ImpactStore: ObservableObject {
    static let shared = ImpactStore()
    
    @Published var impacts: [WatchImpact] = []
    @Published var latestImpact: WatchImpact?
    
    private let maxImpacts = 8 // Reduced from 10 for better memory management
    
    init() {}
    
    func addImpact(_ impact: WatchImpact) {
        // Add new impact
        impacts.append(impact)
        latestImpact = impact
        
        // Keep only last N impacts to manage memory
        if impacts.count > maxImpacts {
            impacts.removeFirst(impacts.count - maxImpacts)
        }
        
        // Memory cleanup - remove old image references
        DispatchQueue.global(qos: .utility).async {
            // Force cleanup of any cached images beyond our limit
            UIImage.performBatteryOptimization()
        }
    }
    
    func clearImpacts() {
        impacts.removeAll()
        latestImpact = nil
        
        // Force memory cleanup
        DispatchQueue.global(qos: .utility).async {
            UIImage.performBatteryOptimization()
        }
    }
}
