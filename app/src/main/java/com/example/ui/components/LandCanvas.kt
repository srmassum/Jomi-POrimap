package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.model.LandShape
import com.example.model.LengthUnit
import com.example.ui.theme.AccentYellow
import com.example.util.BengaliFormat
import com.example.util.LandMath
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun LandCanvas(
    shape: LandShape,
    unit: LengthUnit,
    values: Map<String, String>,
    nTriangles: Int,
    focusedKey: String?,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    fun getRawVal(key: String, fallbackKey: String? = null): Double {
        val fullKey = "${shape.id}_$key"
        val v = BengaliFormat.parseNumber(values[fullKey])
        if (v > 0) return v
        if (fallbackKey != null) {
            val fbKey = "${shape.id}_$fallbackKey"
            return BengaliFormat.parseNumber(values[fbKey])
        }
        return 0.0
    }

    fun getLabel(v: Double): String {
        return if (v > 0) {
            if (v % 1.0 == 0.0) {
                BengaliFormat.toBengaliDigits(v.toLong().toString())
            } else {
                BengaliFormat.formatNumber(v, 1)
            }
        } else ""
    }

    val labelStyle = remember {
        TextStyle(
            color = Color.White,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val canvasW = size.width
        val canvasH = size.height

        val polygonFill = Color(0x38FFFFFF)
        val strokeColor = Color.White
        val strokeWidthPx = 2.5f * density
        val highlightStrokeWidthPx = 4.5f * density
        val vertexRadius = 3.5f * density
        val vertexColor = Color.White
        val hatchColor = Color(0x28FFFFFF)

        fun drawHatchPattern(path: Path) {
            // Draw diagonal grid hatch lines within path bounds
            // Simple diagonal line lattice
            val step = 14f * density
            var x = -canvasH
            while (x < canvasW + canvasH) {
                drawLine(
                    color = hatchColor,
                    start = Offset(x, 0f),
                    end = Offset(x + canvasH, canvasH),
                    strokeWidth = 1.2f * density
                )
                x += step
            }
        }

        fun fitPoints(
            pts: List<Offset>,
            targetW: Float,
            targetH: Float,
            centerX: Float,
            centerY: Float
        ): List<Offset> {
            val minX = pts.minOf { it.x }
            val maxX = pts.maxOf { it.x }
            val minY = pts.minOf { it.y }
            val maxY = pts.maxOf { it.y }

            val rawW = max(1f, maxX - minX)
            val rawH = max(1f, maxY - minY)
            val scale = min(targetW / rawW, targetH / rawH)

            return pts.map { p ->
                Offset(
                    x = centerX - (rawW * scale) / 2f + (p.x - minX) * scale,
                    y = centerY + (rawH * scale) / 2f - (p.y - minY) * scale
                )
            }
        }

        fun drawEdgesAndLabels(
            pts: List<Offset>,
            keys: List<String>,
            fallbackKeys: Map<String, String>? = null
        ) {
            val count = pts.size
            val midCenterX = pts.map { it.x }.average().toFloat()
            val midCenterY = pts.map { it.y }.average().toFloat()

            for (i in 0 until count) {
                val k = keys.getOrNull(i) ?: continue
                val p1 = pts[i]
                val p2 = pts[(i + 1) % count]

                val isFocused = (k == focusedKey)
                if (isFocused) {
                    drawLine(
                        color = AccentYellow,
                        start = p1,
                        end = p2,
                        strokeWidth = highlightStrokeWidthPx
                    )
                }

                val fbKey = fallbackKeys?.get(k)
                val rawVal = getRawVal(k, fbKey)
                val labelText = getLabel(rawVal)

                if (labelText.isNotEmpty()) {
                    val mx = (p1.x + p2.x) / 2f
                    val my = (p1.y + p2.y) / 2f

                    // Normal vector pointing outward
                    var dx = mx - midCenterX
                    var dy = my - midCenterY
                    val len = hypot(dx, dy)
                    if (len > 0.1f) {
                        dx /= len
                        dy /= len
                    } else {
                        dx = 0f
                        dy = -1f
                    }

                    val offsetDistance = 14f * density
                    val textPos = Offset(mx + dx * offsetDistance, my + dy * offsetDistance)

                    val textLayout = textMeasurer.measure(labelText, labelStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = labelText,
                        style = labelStyle,
                        topLeft = Offset(
                            textPos.x - textLayout.size.width / 2f,
                            textPos.y - textLayout.size.height / 2f
                        )
                    )
                }
            }
        }

        when (shape) {
            LandShape.RECTANGLE -> {
                val l = max(1.0, getRawVal("l")).toFloat()
                val w = max(1.0, getRawVal("w")).toFloat()
                val l2 = (if (getRawVal("l2") > 0) getRawVal("l2") else l.toDouble()).toFloat()
                val w2 = (if (getRawVal("w2") > 0) getRawVal("w2") else w.toDouble()).toFloat()
                val avgW = (w + w2) / 2f

                val rawPts = listOf(
                    Offset(0f, 0f),
                    Offset(l, 0f),
                    Offset((l + l2) / 2f, avgW),
                    Offset((l - l2) / 2f, avgW)
                )

                val fitted = fitPoints(
                    pts = rawPts,
                    targetW = canvasW * 0.72f,
                    targetH = canvasH * 0.58f,
                    centerX = canvasW / 2f,
                    centerY = canvasH / 2f
                )

                val path = Path().apply {
                    moveTo(fitted[0].x, fitted[0].y)
                    for (i in 1 until fitted.size) lineTo(fitted[i].x, fitted[i].y)
                    close()
                }

                drawPath(path, color = polygonFill)
                drawPath(path, color = strokeColor, style = Stroke(width = strokeWidthPx))

                drawEdgesAndLabels(
                    pts = fitted,
                    keys = listOf("l", "w2", "l2", "w"),
                    fallbackKeys = mapOf("l2" to "l", "w2" to "w")
                )

                fitted.forEach { pt ->
                    drawCircle(color = vertexColor, radius = vertexRadius, center = pt)
                }
            }

            LandShape.TRIANGLE -> {
                var a = getRawVal("a")
                var b = getRawVal("b")
                var c = getRawVal("c")
                if (!LandMath.isTriangleValid(a, b, c)) {
                    a = 8.0; b = 9.0; c = 10.0
                }

                val x = ((c * c + b * b - a * a) / (2 * c)).toFloat()
                val y = sqrt(max(0.0, b * b - (x * x).toDouble())).toFloat()

                val rawPts = listOf(
                    Offset(0f, 0f),
                    Offset(c.toFloat(), 0f),
                    Offset(x, y)
                )

                val fitted = fitPoints(
                    pts = rawPts,
                    targetW = canvasW * 0.72f,
                    targetH = canvasH * 0.58f,
                    centerX = canvasW / 2f,
                    centerY = canvasH / 2f
                )

                val path = Path().apply {
                    moveTo(fitted[0].x, fitted[0].y)
                    lineTo(fitted[1].x, fitted[1].y)
                    lineTo(fitted[2].x, fitted[2].y)
                    close()
                }

                drawPath(path, color = polygonFill)
                drawPath(path, color = strokeColor, style = Stroke(width = strokeWidthPx))

                drawEdgesAndLabels(
                    pts = fitted,
                    keys = listOf("c", "a", "b")
                )

                fitted.forEach { pt ->
                    drawCircle(color = vertexColor, radius = vertexRadius, center = pt)
                }
            }

            LandShape.TRAPEZOID -> {
                val a = max(1.0, getRawVal("a")).toFloat()
                val b = max(1.0, getRawVal("b")).toFloat()
                val h = max(1.0, getRawVal("h")).toFloat()
                val maxBase = max(a, b)

                val rawPts = listOf(
                    Offset((maxBase - a) / 2f, h),
                    Offset((maxBase + a) / 2f, h),
                    Offset((maxBase + b) / 2f, 0f),
                    Offset((maxBase - b) / 2f, 0f)
                )

                val fitted = fitPoints(
                    pts = rawPts,
                    targetW = canvasW * 0.72f,
                    targetH = canvasH * 0.58f,
                    centerX = canvasW / 2f,
                    centerY = canvasH / 2f
                )

                val path = Path().apply {
                    moveTo(fitted[0].x, fitted[0].y)
                    for (i in 1 until fitted.size) lineTo(fitted[i].x, fitted[i].y)
                    close()
                }

                drawPath(path, color = polygonFill)
                drawPath(path, color = strokeColor, style = Stroke(width = strokeWidthPx))

                drawEdgesAndLabels(
                    pts = fitted,
                    keys = listOf("a", "", "b", "")
                )

                // Dashed height line
                val midTopX = (fitted[0].x + fitted[1].x) / 2f
                val topY = fitted[0].y
                val botY = fitted[3].y
                val isHFocused = (focusedKey == "h")

                drawLine(
                    color = if (isHFocused) AccentYellow else strokeColor,
                    start = Offset(midTopX, topY),
                    end = Offset(midTopX, botY),
                    strokeWidth = if (isHFocused) highlightStrokeWidthPx else 1.8f * density,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )

                val hVal = getRawVal("h")
                val hText = getLabel(hVal)
                if (hText.isNotEmpty()) {
                    val textLayout = textMeasurer.measure(hText, labelStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = hText,
                        style = labelStyle,
                        topLeft = Offset(midTopX + 8f, (topY + botY) / 2f - textLayout.size.height / 2f)
                    )
                }

                fitted.forEach { pt ->
                    drawCircle(color = vertexColor, radius = vertexRadius, center = pt)
                }
            }

            LandShape.CIRCLE -> {
                val center = Offset(canvasW / 2f, canvasH / 2f)
                val radius = min(canvasW * 0.32f, canvasH * 0.38f)

                drawCircle(color = polygonFill, radius = radius, center = center)
                drawCircle(
                    color = strokeColor,
                    radius = radius,
                    center = center,
                    style = Stroke(width = strokeWidthPx)
                )

                val isRFocused = (focusedKey == "r")
                val endPoint = Offset(center.x + radius, center.y)

                drawLine(
                    color = if (isRFocused) AccentYellow else strokeColor,
                    start = center,
                    end = endPoint,
                    strokeWidth = if (isRFocused) highlightStrokeWidthPx else 1.8f * density,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 6f), 0f)
                )

                drawCircle(color = vertexColor, radius = vertexRadius, center = center)

                val rVal = getRawVal("r")
                val rText = getLabel(rVal)
                if (rText.isNotEmpty()) {
                    val fullRText = "r = $rText"
                    val textLayout = textMeasurer.measure(fullRText, labelStyle)
                    drawText(
                        textMeasurer = textMeasurer,
                        text = fullRText,
                        style = labelStyle,
                        topLeft = Offset(center.x + radius * 0.35f, center.y - textLayout.size.height - 4f)
                    )
                }
            }

            LandShape.IRREGULAR -> {
                val slotW = (canvasW * 0.88f) / nTriangles
                for (i in 1..nTriangles) {
                    var a = getRawVal("${i}a")
                    var b = getRawVal("${i}b")
                    var c = getRawVal("${i}c")
                    if (!LandMath.isTriangleValid(a, b, c)) {
                        a = 6.0; b = 7.0; c = 8.0
                    }

                    val x = ((c * c + b * b - a * a) / (2 * c)).toFloat()
                    val y = sqrt(max(0.0, b * b - (x * x).toDouble())).toFloat()

                    val rawPts = listOf(
                        Offset(0f, 0f),
                        Offset(c.toFloat(), 0f),
                        Offset(x, y)
                    )

                    val cx = (canvasW * 0.06f) + slotW * (i - 0.5f)
                    val fitted = fitPoints(
                        pts = rawPts,
                        targetW = slotW * 0.78f,
                        targetH = canvasH * 0.52f,
                        centerX = cx,
                        centerY = canvasH / 2f
                    )

                    val path = Path().apply {
                        moveTo(fitted[0].x, fitted[0].y)
                        lineTo(fitted[1].x, fitted[1].y)
                        lineTo(fitted[2].x, fitted[2].y)
                        close()
                    }

                    drawPath(path, color = polygonFill)
                    drawPath(path, color = strokeColor, style = Stroke(width = strokeWidthPx))

                    if (nTriangles <= 3) {
                        drawEdgesAndLabels(
                            pts = fitted,
                            keys = listOf("${i}c", "${i}a", "${i}b")
                        )
                    }

                    fitted.forEach { pt ->
                        drawCircle(color = vertexColor, radius = vertexRadius, center = pt)
                    }
                }
            }
        }
    }
}
