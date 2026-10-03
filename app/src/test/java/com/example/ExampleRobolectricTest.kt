package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.model.LandShape
import com.example.model.LengthUnit
import com.example.util.BengaliFormat
import com.example.util.LandMath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("ভূমি পরিমাপ", appName)
    }

    @Test
    fun `test rectangle calculation`() {
        val values = mapOf("rect_l" to "৬০", "rect_w" to "৪০")
        val area = LandMath.calculateAreaSqFt(
            shape = LandShape.RECTANGLE,
            unit = LengthUnit.FEET,
            values = values,
            nTriangles = 2
        )
        assertEquals(2400.0, area, 0.01)
        val shotok = LandMath.convertSqFtTo(area, AreaUnit.SHOTOK, KathaStandard.STANDARD_720)
        // 2400 / 435.6 = 5.5096 shotok
        assertTrue(shotok > 5.50 && shotok < 5.52)
    }

    @Test
    fun `test bengali number format and parse`() {
        val bnStr = BengaliFormat.toBengaliDigits("1234.5")
        assertEquals("১২৩৪.৫", bnStr)
        val parsed = BengaliFormat.parseNumber("১২৩৪.৫")
        assertEquals(1234.5, parsed, 0.001)
    }
}
