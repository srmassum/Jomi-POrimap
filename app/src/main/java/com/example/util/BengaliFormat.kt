package com.example.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object BengaliFormat {
    private val bengaliDigits = charArrayOf('০', '১', '২', '৩', '৪', '৫', '৬', '৭', '৮', '৯')
    private val englishDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')

    fun toBengaliDigits(input: String): String {
        val sb = java.lang.StringBuilder(input.length)
        for (ch in input) {
            if (ch in '0'..'9') {
                sb.append(bengaliDigits[ch - '0'])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun toEnglishDigits(input: String): String {
        val sb = java.lang.StringBuilder(input.length)
        for (ch in input) {
            val idx = bengaliDigits.indexOf(ch)
            if (idx != -1) {
                sb.append(englishDigits[idx])
            } else {
                sb.append(ch)
            }
        }
        return sb.toString()
    }

    fun parseNumber(input: String?): Double {
        if (input.isNullOrBlank()) return 0.0
        val englishStr = toEnglishDigits(input).replace(",", "").trim()
        return englishStr.toDoubleOrNull() ?: 0.0
    }

    fun formatNumber(value: Double, decimals: Int = 2): String {
        if (value.isNaN() || value.isInfinite()) return "০"
        val symbols = DecimalFormatSymbols(Locale.US)
        val pattern = buildString {
            append("#,##0")
            if (decimals > 0) {
                append(".")
                repeat(decimals) { append("0") }
            }
        }
        val df = DecimalFormat(pattern, symbols)
        val formatted = df.format(value)
        return toBengaliDigits(formatted)
    }

    fun formatDateBn(timestamp: Long): String {
        val sdf = SimpleDateFormat("d MMMM yyyy, h:mm a", Locale.ENGLISH)
        val dateStr = sdf.format(Date(timestamp))
        val withBengaliMonths = dateStr
            .replace("January", "জানুয়ারি")
            .replace("February", "ফেব্রুয়ারি")
            .replace("March", "মার্চ")
            .replace("April", "এপ্রিল")
            .replace("May", "মে")
            .replace("June", "জুন")
            .replace("July", "জুলাই")
            .replace("August", "আগস্ট")
            .replace("September", "সেপ্টেম্বর")
            .replace("October", "অক্টোবর")
            .replace("November", "নভেম্বর")
            .replace("December", "ডিসেম্বর")
            .replace("AM", "পূর্বাহ্ন")
            .replace("PM", "অপরাহ্ন")
        return toBengaliDigits(withBengaliMonths)
    }

    fun serializeMap(map: Map<String, String>): String {
        return map.entries.joinToString(";") { "${it.key}=${it.value}" }
    }

    fun deserializeMap(str: String): Map<String, String> {
        if (str.isBlank()) return emptyMap()
        return str.split(";").mapNotNull {
            val parts = it.split("=", limit = 2)
            if (parts.size == 2) parts[0] to parts[1] else null
        }.toMap()
    }
}
