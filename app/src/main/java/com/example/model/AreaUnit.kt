package com.example.model

enum class AreaUnit(
    val id: String,
    val titleBn: String,
    val decimals: Int,
    val hexColor: Long
) {
    SHOTOK("shotok", "শতাংশ", 3, 0xFF10B981),
    KATHA("katha", "কাঠা", 3, 0xFFF59E0B),
    BIGHA("bigha", "বিঘা", 4, 0xFFF43F5E),
    ACRE("acre", "একর", 5, 0xFF8B5CF6),
    SQ_METER("sqm", "বর্গমিটার", 2, 0xFF0EA5E9),
    SQ_FEET("sqft", "বর্গফুট", 2, 0xFF64748B),
    SQ_HAT("sqhat", "বর্গহাত", 2, 0xFF14B8A6);

    companion object {
        fun fromId(id: String): AreaUnit = entries.firstOrNull { it.id == id } ?: SQ_FEET
    }
}
