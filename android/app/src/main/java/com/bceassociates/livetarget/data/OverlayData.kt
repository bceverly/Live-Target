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
    BOTTOM_RIGHT("Bottom Right"),
}

enum class CartridgeType(val displayName: String) {
    BLACK_POWDER("Black Powder"),
    METALLIC_CARTRIDGE("Metallic Cartridge"),
}

enum class AmmoType(val displayName: String) {
    FACTORY("Factory Load"),
    HANDLOAD("Handload"),
}

enum class BlackPowderType(val displayName: String) {
    ONEF("1F"),
    TWOF("2F"),
    THREEF("3F"),
    FOURF("4F"),
}

enum class ProjectileType(val displayName: String) {
    ROUND_BALL("Round Ball"),
    CONICAL("Conical"),
    SABOTTED_BULLET("Sabotted Bullet"),
    POWERBELT_BULLET("PowerBelt Bullet"),
}

data class OverlaySettings(
    val enabled: Boolean,
    val position: OverlayPosition,
    // in grains
    val bulletWeight: Double,
    val cartridgeType: CartridgeType,
    val ammoType: AmmoType,
    val factoryAmmoName: String,
    val handloadPowder: String,
    // in grains
    val handloadCharge: Double,
    val blackPowderType: BlackPowderType,
    val projectileType: ProjectileType,
    // in grains
    val blackPowderCharge: Double,
    val selectedCaliberName: String,
) {
    fun getAmmoDescription(): String {
        return when (cartridgeType) {
            CartridgeType.BLACK_POWDER -> {
                "${blackPowderType.displayName} ${String.format("%.1f", blackPowderCharge)}gr"
            }
            CartridgeType.METALLIC_CARTRIDGE -> {
                when (ammoType) {
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
        }
    }

    fun getOverlayText(): String {
        val dateFormatter = SimpleDateFormat("M/d/yy h:mm a", Locale.getDefault())
        val date = dateFormatter.format(Date())
        val weight = String.format("%.0f", bulletWeight)
        val ammo = getAmmoDescription()

        return when (cartridgeType) {
            CartridgeType.BLACK_POWDER -> {
                "$date\n$selectedCaliberName ${weight}gr ${projectileType.displayName}\n$ammo"
            }
            CartridgeType.METALLIC_CARTRIDGE -> {
                "$date\n$selectedCaliberName ${weight}gr\n$ammo"
            }
        }
    }
}
