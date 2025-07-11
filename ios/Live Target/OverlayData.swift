//
//  OverlayData.swift
//  Live Target
//
//  Created by Bryan Everly on 7/11/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Foundation

enum OverlayPosition: String, CaseIterable, Identifiable {
    case topLeft = "Top Left"
    case topCenter = "Top Center"
    case topRight = "Top Right"
    case bottomLeft = "Bottom Left"
    case bottomCenter = "Bottom Center"
    case bottomRight = "Bottom Right"
    
    var id: String { self.rawValue }
}

enum AmmoType: String, CaseIterable, Identifiable {
    case factory = "Factory Load"
    case handload = "Handload"
    
    var id: String { self.rawValue }
}

struct OverlaySettings {
    let enabled: Bool
    let position: OverlayPosition
    let bulletWeight: Double // in grains
    let ammoType: AmmoType
    let factoryAmmoName: String
    let handloadPowder: String
    let handloadCharge: Double // in grains
    let selectedCaliberName: String
    
    func getAmmoDescription() -> String {
        switch ammoType {
        case .factory:
            return factoryAmmoName.isEmpty ? "Factory Load" : factoryAmmoName
        case .handload:
            if handloadPowder.isEmpty {
                return "Handload"
            } else {
                return "\(handloadPowder) \(String(format: "%.1f", handloadCharge))gr"
            }
        }
    }
    
    func getOverlayText() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .short
        dateFormatter.timeStyle = .short
        
        let date = dateFormatter.string(from: Date())
        let weight = String(format: "%.0f", bulletWeight)
        let ammo = getAmmoDescription()
        
        return "\(date)\n\(selectedCaliberName) \(weight)gr\n\(ammo)"
    }
}