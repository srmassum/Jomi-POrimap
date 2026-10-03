package com.example.model

enum class KathaStandard(
    val id: String,
    val titleBn: String,
    val kathaSqFt: Double
) {
    STANDARD_720("720", "৭২০ বর্গফুট", 720.0),
    STANDARD_165("165", "১.৬৫ শতাংশ", 718.74);

    val bighaSqFt: Double get() = kathaSqFt * 20.0

    companion object {
        fun fromId(id: String): KathaStandard =
            if (id == "165") STANDARD_165 else STANDARD_720
    }
}
