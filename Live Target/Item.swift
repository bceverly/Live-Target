//
//  Item.swift
//  Live Target
//
//  Created by Bryan Everly on 7/7/25.
//

import Foundation
import SwiftData

@Model
final class Item {
    var timestamp: Date
    
    init(timestamp: Date) {
        self.timestamp = timestamp
    }
}
