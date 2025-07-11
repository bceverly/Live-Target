package com.bceassociates.livetarget.data

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class OverlayPosition(val displayName: String) {
    TOP_LEFT("Top Left"),
    TOP_CENTER("Top Center"),
    TOP_RIGHT("Top Right"),
    BOTTOM_LEFT("Bottom Left"),
    BOTTOM_CENTER("Bottom Center"),
    BOTTOM_RIGHT("Bottom Right")
}

enum class AmmoType(val displayName: String) {
    FACTORY("Factory Load"),
    HANDLOAD("Handload")
}

data class OverlaySettings(
    val enabled: Boolean,
    val position: OverlayPosition,
    val bulletWeight: Double, // in grains
    val ammoType: AmmoType,
    val factoryAmmoName: String,
    val handloadPowder: String,
    val handloadCharge: Double, // in grains
    val selectedCaliberName: String
) {
    fun getAmmoDescription(): String {
        return when (ammoType) {
            AmmoType.FACTORY -> {
                if (factoryAmmoName.isBlank()) "Factory Load" else factoryAmmoName
            }
            AmmoType.HANDLOAD -> {
                if (handloadPowder.isBlank()) {
                    "Handload"
                } else {
                    "$handloadPowder ${String.format("%.1f", handloadCharge)}gr"
                }
            }
        }
    }
    
    fun getOverlayText(): String {
        val dateFormatter = SimpleDateFormat("M/d/yy h:mm a", Locale.getDefault())
        val date = dateFormatter.format(Date())
        val weight = String.format("%.0f", bulletWeight)
        val ammo = getAmmoDescription()
        
        return "$date\n$selectedCaliberName ${weight}gr\n$ammo"
    }
}