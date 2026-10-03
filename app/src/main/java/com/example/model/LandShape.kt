package com.example.model

enum class LandShape(
    val id: String,
    val titleBn: String,
    val hexColor: Long
) {
    RECTANGLE("rect", "আয়তক্ষেত্র", 0xFF4F46E5),
    TRIANGLE("tri", "ত্রিভুজ", 0xFF0369A1),
    TRAPEZOID("trap", "ট্রাপিজিয়াম", 0xFF047857),
    CIRCLE("circ", "বৃত্ত", 0xFFB45309),
    IRREGULAR("irr", "অনিয়মিত", 0xFFE11D48);

    companion object {
        fun fromId(id: String): LandShape = entries.firstOrNull { it.id == id } ?: RECTANGLE
    }
}
