package com.example.model

enum class LengthUnit(
    val id: String,
    val titleBn: String,
    val shortBn: String,
    val feetRatio: Double
) {
    FEET("ft", "ফুট", "ফুট", 1.0),
    METER("m", "মিটার", "মি", 3.280839895),
    HAT("hat", "হাত", "হাত", 1.5);

    companion object {
        fun fromId(id: String): LengthUnit = entries.firstOrNull { it.id == id } ?: FEET
    }
}
