package com.bceassociates.livetarget.data

data class Caliber(
    val name: String,
    val category: String,
    val diameterInches: Double,
    val common: Boolean,
) {
    val pixelSize: Int
        get() = (diameterInches * 100).toInt()
}

object CaliberData {
    val calibers =
        listOf(
            Caliber(".17 HMR", "Rimfire", 0.172, true),
            Caliber(".17 Remington", "Centerfire Rifle", 0.172, false),
            Caliber(".22 Short", "Rimfire", 0.223, true),
            Caliber(".22 Long Rifle", "Rimfire", 0.223, true),
            Caliber(".22 WMR", "Rimfire", 0.224, true),
            Caliber(".22 Hornet", "Centerfire Rifle", 0.224, false),
            Caliber(".223 Remington", "Centerfire Rifle", 0.224, true),
            Caliber("5.56×45mm NATO", "Centerfire Rifle", 0.224, true),
            Caliber(".243 Winchester", "Centerfire Rifle", 0.243, true),
            Caliber("6mm Remington", "Centerfire Rifle", 0.243, false),
            Caliber("6.5 Carcano", "Centerfire Rifle", 0.268, false),
            Caliber("6.5×55mm Swedish", "Centerfire Rifle", 0.264, false),
            Caliber("6.5 Creedmoor", "Centerfire Rifle", 0.264, true),
            Caliber("6.5 Grendel", "Centerfire Rifle", 0.264, false),
            Caliber(".270 Winchester", "Centerfire Rifle", 0.277, true),
            Caliber("7mm Remington Magnum", "Centerfire Rifle", 0.284, true),
            Caliber("7mm-08 Remington", "Centerfire Rifle", 0.284, false),
            Caliber("7×57mm Mauser", "Centerfire Rifle", 0.284, false),
            Caliber(".30 Carbine", "Centerfire Rifle", 0.308, false),
            Caliber(".30-30 Winchester", "Centerfire Rifle", 0.308, true),
            Caliber(".308 Winchester", "Centerfire Rifle", 0.308, true),
            Caliber("7.62×51mm NATO", "Centerfire Rifle", 0.308, true),
            Caliber(".30-06 Springfield", "Centerfire Rifle", 0.308, true),
            Caliber("7.62×39mm", "Centerfire Rifle", 0.311, true),
            Caliber("7.62×54mmR", "Centerfire Rifle", 0.311, false),
            Caliber(".300 Winchester Magnum", "Centerfire Rifle", 0.308, true),
            Caliber(".300 Blackout", "Centerfire Rifle", 0.308, true),
            Caliber("8mm Mauser", "Centerfire Rifle", 0.323, false),
            Caliber(".338 Lapua Magnum", "Centerfire Rifle", 0.338, false),
            Caliber(".25 ACP", "Centerfire Pistol", 0.251, false),
            Caliber(".32 ACP", "Centerfire Pistol", 0.312, false),
            Caliber(".380 ACP", "Centerfire Pistol", 0.355, true),
            Caliber("9mm Luger", "Centerfire Pistol", 0.355, true),
            Caliber("9×18mm Makarov", "Centerfire Pistol", 0.365, false),
            Caliber(".38 Special", "Centerfire Pistol", 0.357, true),
            Caliber(".357 Magnum", "Centerfire Pistol", 0.357, true),
            Caliber(".40 S&W", "Centerfire Pistol", 0.400, true),
            Caliber("10mm Auto", "Centerfire Pistol", 0.400, false),
            Caliber(".44 Special", "Centerfire Pistol", 0.429, false),
            Caliber(".44 Magnum", "Centerfire Pistol", 0.429, true),
            Caliber(".45 ACP", "Centerfire Pistol", 0.452, true),
            Caliber(".45 Colt", "Centerfire Pistol", 0.452, false),
            Caliber(".50 AE", "Centerfire Pistol", 0.500, false),
            Caliber(".410 Bore", "Shotgun", 0.410, false),
            Caliber("20 Gauge", "Shotgun", 0.615, false),
            Caliber("16 Gauge", "Shotgun", 0.670, false),
            Caliber("12 Gauge", "Shotgun", 0.729, false),
            Caliber("10 Gauge", "Shotgun", 0.775, false),
        )

    val commonCalibers: List<Caliber>
        get() = calibers.filter { it.common }.sortedBy { it.name }

    val allCalibers: List<Caliber>
        get() = calibers.sortedBy { it.name }

    val pistolCalibers: List<Caliber>
        get() = calibers.filter { it.category == "Centerfire Pistol" }.sortedBy { it.name }

    val rifleCalibers: List<Caliber>
        get() = calibers.filter { it.category.contains("Rifle") }.sortedBy { it.name }

    val rimfireCalibers: List<Caliber>
        get() = calibers.filter { it.category == "Rimfire" }.sortedBy { it.name }

    val shotgunCalibers: List<Caliber>
        get() = calibers.filter { it.category == "Shotgun" }.sortedBy { it.name }

    fun findCaliberByName(name: String): Caliber? {
        return calibers.find { it.name == name }
    }

    fun findCaliberByDiameter(diameter: Double): Caliber? {
        return calibers.find { kotlin.math.abs(it.diameterInches - diameter) < 0.001 }
    }
}
