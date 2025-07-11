//
//  CaliberData.swift
//  Live Target
//
//  Created by Bryan Everly on 7/11/25.
//  Copyright © 2025 BCEAssociates, Inc. All rights reserved.
//

import Foundation

struct Caliber: Identifiable, Hashable {
    let id = UUID()
    let name: String
    let category: String
    let diameterInches: Double
    let common: Bool
    
    var displayName: String {
        return name
    }
    
    var pixelSize: Int {
        return Int(diameterInches * 100)
    }
}

class CaliberData: ObservableObject {
    static let shared = CaliberData()
    
    let calibers: [Caliber] = [
        Caliber(name: ".17 HMR", category: "Rimfire", diameterInches: 0.172, common: true),
        Caliber(name: ".17 Remington", category: "Centerfire Rifle", diameterInches: 0.172, common: false),
        Caliber(name: ".22 Short", category: "Rimfire", diameterInches: 0.223, common: true),
        Caliber(name: ".22 Long Rifle", category: "Rimfire", diameterInches: 0.223, common: true),
        Caliber(name: ".22 WMR", category: "Rimfire", diameterInches: 0.224, common: true),
        Caliber(name: ".22 Hornet", category: "Centerfire Rifle", diameterInches: 0.224, common: false),
        Caliber(name: ".223 Remington", category: "Centerfire Rifle", diameterInches: 0.224, common: true),
        Caliber(name: "5.56×45mm NATO", category: "Centerfire Rifle", diameterInches: 0.224, common: true),
        Caliber(name: ".243 Winchester", category: "Centerfire Rifle", diameterInches: 0.243, common: true),
        Caliber(name: "6mm Remington", category: "Centerfire Rifle", diameterInches: 0.243, common: false),
        Caliber(name: "6.5 Carcano", category: "Centerfire Rifle", diameterInches: 0.268, common: false),
        Caliber(name: "6.5×55mm Swedish", category: "Centerfire Rifle", diameterInches: 0.264, common: false),
        Caliber(name: "6.5 Creedmoor", category: "Centerfire Rifle", diameterInches: 0.264, common: true),
        Caliber(name: "6.5 Grendel", category: "Centerfire Rifle", diameterInches: 0.264, common: false),
        Caliber(name: ".270 Winchester", category: "Centerfire Rifle", diameterInches: 0.277, common: true),
        Caliber(name: "7mm Remington Magnum", category: "Centerfire Rifle", diameterInches: 0.284, common: true),
        Caliber(name: "7mm-08 Remington", category: "Centerfire Rifle", diameterInches: 0.284, common: false),
        Caliber(name: "7×57mm Mauser", category: "Centerfire Rifle", diameterInches: 0.284, common: false),
        Caliber(name: ".30 Carbine", category: "Centerfire Rifle", diameterInches: 0.308, common: false),
        Caliber(name: ".30-30 Winchester", category: "Centerfire Rifle", diameterInches: 0.308, common: true),
        Caliber(name: ".308 Winchester", category: "Centerfire Rifle", diameterInches: 0.308, common: true),
        Caliber(name: "7.62×51mm NATO", category: "Centerfire Rifle", diameterInches: 0.308, common: true),
        Caliber(name: ".30-06 Springfield", category: "Centerfire Rifle", diameterInches: 0.308, common: true),
        Caliber(name: "7.62×39mm", category: "Centerfire Rifle", diameterInches: 0.311, common: true),
        Caliber(name: "7.62×54mmR", category: "Centerfire Rifle", diameterInches: 0.311, common: false),
        Caliber(name: ".300 Winchester Magnum", category: "Centerfire Rifle", diameterInches: 0.308, common: true),
        Caliber(name: ".300 Blackout", category: "Centerfire Rifle", diameterInches: 0.308, common: true),
        Caliber(name: "8mm Mauser", category: "Centerfire Rifle", diameterInches: 0.323, common: false),
        Caliber(name: ".338 Lapua Magnum", category: "Centerfire Rifle", diameterInches: 0.338, common: false),
        Caliber(name: ".25 ACP", category: "Centerfire Pistol", diameterInches: 0.251, common: false),
        Caliber(name: ".32 ACP", category: "Centerfire Pistol", diameterInches: 0.312, common: false),
        Caliber(name: ".380 ACP", category: "Centerfire Pistol", diameterInches: 0.355, common: true),
        Caliber(name: "9mm Luger", category: "Centerfire Pistol", diameterInches: 0.355, common: true),
        Caliber(name: "9×18mm Makarov", category: "Centerfire Pistol", diameterInches: 0.365, common: false),
        Caliber(name: ".38 Special", category: "Centerfire Pistol", diameterInches: 0.357, common: true),
        Caliber(name: ".357 Magnum", category: "Centerfire Pistol", diameterInches: 0.357, common: true),
        Caliber(name: ".40 S&W", category: "Centerfire Pistol", diameterInches: 0.400, common: true),
        Caliber(name: "10mm Auto", category: "Centerfire Pistol", diameterInches: 0.400, common: false),
        Caliber(name: ".44 Special", category: "Centerfire Pistol", diameterInches: 0.429, common: false),
        Caliber(name: ".44 Magnum", category: "Centerfire Pistol", diameterInches: 0.429, common: true),
        Caliber(name: ".45 ACP", category: "Centerfire Pistol", diameterInches: 0.452, common: true),
        Caliber(name: ".45 Colt", category: "Centerfire Pistol", diameterInches: 0.452, common: false),
        Caliber(name: ".50 AE", category: "Centerfire Pistol", diameterInches: 0.500, common: false),
        Caliber(name: ".410 Bore", category: "Shotgun", diameterInches: 0.410, common: false),
        Caliber(name: "20 Gauge", category: "Shotgun", diameterInches: 0.615, common: false),
        Caliber(name: "16 Gauge", category: "Shotgun", diameterInches: 0.670, common: false),
        Caliber(name: "12 Gauge", category: "Shotgun", diameterInches: 0.729, common: false),
        Caliber(name: "10 Gauge", category: "Shotgun", diameterInches: 0.775, common: false)
    ]
    
    var commonCalibers: [Caliber] {
        return calibers.filter { $0.common }.sorted { $0.name < $1.name }
    }
    
    var allCalibers: [Caliber] {
        return calibers.sorted { $0.name < $1.name }
    }
    
    var pistolCalibers: [Caliber] {
        return calibers.filter { $0.category == "Centerfire Pistol" }.sorted { $0.name < $1.name }
    }
    
    var rifleCalibers: [Caliber] {
        return calibers.filter { $0.category.contains("Rifle") }.sorted { $0.name < $1.name }
    }
    
    var rimfireCalibers: [Caliber] {
        return calibers.filter { $0.category == "Rimfire" }.sorted { $0.name < $1.name }
    }
    
    func findCaliber(byName name: String) -> Caliber? {
        return calibers.first { $0.name == name }
    }
    
    func findCaliber(byDiameter diameter: Double) -> Caliber? {
        return calibers.first { abs($0.diameterInches - diameter) < 0.001 }
    }
}