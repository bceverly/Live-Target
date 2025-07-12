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

enum CartridgeType: String, CaseIterable, Identifiable {
    case blackPowder = "Black Powder"
    case metallicCartridge = "Metallic Cartridge"
    
    var id: String { self.rawValue }
}

enum AmmoType: String, CaseIterable, Identifiable {
    case factory = "Factory Load"
    case handload = "Handload"
    
    var id: String { self.rawValue }
}

enum BlackPowderType: String, CaseIterable, Identifiable {
    case onef = "1F"
    case twof = "2F"
    case threef = "3F"
    case fourf = "4F"
    
    var id: String { self.rawValue }
}

enum ProjectileType: String, CaseIterable, Identifiable {
    case roundBall = "Round Ball"
    case conical = "Conical"
    case sabottedBullet = "Sabotted Bullet"
    case powerBeltBullet = "PowerBelt Bullet"
    
    var id: String { self.rawValue }
}

struct OverlaySettings {
    let enabled: Bool
    let position: OverlayPosition
    let bulletWeight: Double // in grains
    let cartridgeType: CartridgeType
    let ammoType: AmmoType
    let factoryAmmoName: String
    let handloadPowder: String
    let handloadCharge: Double // in grains
    let blackPowderType: BlackPowderType
    let projectileType: ProjectileType
    let blackPowderCharge: Double // in grains
    let selectedCaliberName: String
    
    func getAmmoDescription() -> String {
        switch cartridgeType {
        case .blackPowder:
            return "\(blackPowderType.rawValue) \(String(format: "%.1f", blackPowderCharge))gr"
        case .metallicCartridge:
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
    }
    
    func getOverlayText() -> String {
        let dateFormatter = DateFormatter()
        dateFormatter.dateStyle = .short
        dateFormatter.timeStyle = .short
        
        let date = dateFormatter.string(from: Date())
        let weight = String(format: "%.0f", bulletWeight)
        let ammo = getAmmoDescription()
        
        switch cartridgeType {
        case .blackPowder:
            return "\(date)\n\(selectedCaliberName) \(weight)gr \(projectileType.rawValue)\n\(ammo)"
        case .metallicCartridge:
            return "\(date)\n\(selectedCaliberName) \(weight)gr\n\(ammo)"
        }
    }
}