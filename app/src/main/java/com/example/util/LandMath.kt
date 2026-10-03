package com.example.util

import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.model.LandShape
import com.example.model.LengthUnit
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.sqrt

object LandMath {
    const val SHOTOK_SQFT = 435.6
    const val ACRE_SQFT = 43560.0
    const val SQM_SQFT = 10.763910417
    const val SQHAT_SQFT = 2.25

    fun heron(a: Double, b: Double, c: Double): Double {
        if (a <= 0 || b <= 0 || c <= 0) return Double.NaN
        if (a + b <= c || a + c <= b || b + c <= a) return Double.NaN
        val s = (a + b + c) / 2.0
        val areaSq = s * (s - a) * (s - b) * (s - c)
        return if (areaSq > 0) sqrt(areaSq) else Double.NaN
    }

    fun isTriangleValid(a: Double, b: Double, c: Double): Boolean {
        if (a <= 0 || b <= 0 || c <= 0) return false
        return (a + b > c) && (a + c > b) && (b + c > a)
    }

    fun calculateAreaSqFt(
        shape: LandShape,
        unit: LengthUnit,
        values: Map<String, String>,
        nTriangles: Int
    ): Double {
        val factor = unit.feetRatio

        fun num(key: String): Double {
            val fullKey = "${shape.id}_$key"
            val raw = BengaliFormat.parseNumber(values[fullKey])
            return raw * factor
        }

        return when (shape) {
            LandShape.RECTANGLE -> {
                val l1 = num("l")
                val w1 = num("w")
                if (l1 <= 0 || w1 <= 0) return Double.NaN

                val rawL2 = num("l2")
                val rawW2 = num("w2")
                val l2 = if (rawL2 > 0) rawL2 else l1
                val w2 = if (rawW2 > 0) rawW2 else w1

                val avgL = (l1 + l2) / 2.0
                val avgW = (w1 + w2) / 2.0
                avgL * avgW
            }

            LandShape.TRIANGLE -> {
                val a = num("a")
                val b = num("b")
                val c = num("c")
                heron(a, b, c)
            }

            LandShape.TRAPEZOID -> {
                val a = num("a")
                val b = num("b")
                val h = num("h")
                if (a <= 0 || b <= 0 || h <= 0) Double.NaN
                else ((a + b) / 2.0) * h
            }

            LandShape.CIRCLE -> {
                val r = num("r")
                if (r <= 0) Double.NaN
                else PI * r * r
            }

            LandShape.IRREGULAR -> {
                var total = 0.0
                var hasValid = false
                for (i in 1..nTriangles) {
                    val a = num("${i}a")
                    val b = num("${i}b")
                    val c = num("${i}c")
                    val h = heron(a, b, c)
                    if (!h.isNaN() && h > 0) {
                        total += h
                        hasValid = true
                    }
                }
                if (hasValid) total else Double.NaN
            }
        }
    }

    fun getSqFtPerUnit(unit: AreaUnit, kathaStandard: KathaStandard): Double {
        return when (unit) {
            AreaUnit.SHOTOK -> SHOTOK_SQFT
            AreaUnit.KATHA -> kathaStandard.kathaSqFt
            AreaUnit.BIGHA -> kathaStandard.bighaSqFt
            AreaUnit.ACRE -> ACRE_SQFT
            AreaUnit.SQ_METER -> SQM_SQFT
            AreaUnit.SQ_FEET -> 1.0
            AreaUnit.SQ_HAT -> SQHAT_SQFT
        }
    }

    fun convertSqFtTo(sqFt: Double, targetUnit: AreaUnit, kathaStandard: KathaStandard): Double {
        if (sqFt.isNaN() || sqFt <= 0) return 0.0
        val base = getSqFtPerUnit(targetUnit, kathaStandard)
        return sqFt / base
    }

    fun convertUnitToUnit(
        amount: Double,
        fromUnit: AreaUnit,
        toUnit: AreaUnit,
        kathaStandard: KathaStandard
    ): Double {
        if (amount <= 0) return 0.0
        val sqFt = amount * getSqFtPerUnit(fromUnit, kathaStandard)
        return sqFt / getSqFtPerUnit(toUnit, kathaStandard)
    }

    fun getValidationWarning(
        shape: LandShape,
        values: Map<String, String>,
        nTriangles: Int
    ): String? {
        fun rawNum(key: String): Double {
            return BengaliFormat.parseNumber(values["${shape.id}_$key"])
        }

        when (shape) {
            LandShape.TRIANGLE -> {
                val a = rawNum("a")
                val b = rawNum("b")
                val c = rawNum("c")
                if (a > 0 && b > 0 && c > 0 && !isTriangleValid(a, b, c)) {
                    return "এই তিন বাহু দিয়ে ত্রিভুজ হয় না — দুই বাহুর যোগফল তৃতীয়টির চেয়ে বড় হতে হবে।"
                }
            }

            LandShape.IRREGULAR -> {
                for (i in 1..nTriangles) {
                    val a = rawNum("${i}a")
                    val b = rawNum("${i}b")
                    val c = rawNum("${i}c")
                    if (a > 0 && b > 0 && c > 0 && !isTriangleValid(a, b, c)) {
                        return "ত্রিভুজ ${BengaliFormat.toBengaliDigits(i.toString())} বৈধ নয় — বাহুর মাপ মিলিয়ে দেখুন।"
                    }
                }
            }

            LandShape.RECTANGLE -> {
                val l1 = rawNum("l")
                val l2 = rawNum("l2")
                val w1 = rawNum("w")
                val w2 = rawNum("w2")

                val checkL = l1 > 0 && l2 > 0 && (abs(l1 - l2) / max(l1, l2)) > 0.10
                val checkW = w1 > 0 && w2 > 0 && (abs(w1 - w2) / max(w1, w2)) > 0.10

                if (checkL || checkW) {
                    return "দুই পাশের মাপে ১০%-এর বেশি পার্থক্য — গড় পদ্ধতিতে হিসাব আনুমানিক। নিখুঁত মাপের জন্য অনিয়মিত মোডে ত্রিভুজ ভাগ করে মাপুন।"
                }
            }

            else -> Unit
        }
        return null
    }
}
