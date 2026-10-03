package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.util.BengaliFormat
import com.example.util.LandMath

@Composable
fun ResultTiles(
    totalSqFt: Double,
    kathaStandard: KathaStandard,
    modifier: Modifier = Modifier
) {
    val isValid = totalSqFt > 0

    val shotokVal = if (isValid) LandMath.convertSqFtTo(totalSqFt, AreaUnit.SHOTOK, kathaStandard) else 0.0
    val kathaVal = if (isValid) LandMath.convertSqFtTo(totalSqFt, AreaUnit.KATHA, kathaStandard) else 0.0
    val bighaVal = if (isValid) LandMath.convertSqFtTo(totalSqFt, AreaUnit.BIGHA, kathaStandard) else 0.0
    val acreVal = if (isValid) LandMath.convertSqFtTo(totalSqFt, AreaUnit.ACRE, kathaStandard) else 0.0
    val sqmVal = if (isValid) LandMath.convertSqFtTo(totalSqFt, AreaUnit.SQ_METER, kathaStandard) else 0.0
    val sqftVal = if (isValid) totalSqFt else 0.0
    val sqhatVal = if (isValid) LandMath.convertSqFtTo(totalSqFt, AreaUnit.SQ_HAT, kathaStandard) else 0.0

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Featured Hero Tile: Shotok (শতাংশ / ডেসিমাল)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF047857), Color(0xFF0F766E))
                    )
                )
                .border(1.5.dp, Color(0xFF34D399), RoundedCornerShape(18.dp))
                .padding(horizontal = 16.dp, vertical = 14.dp)
                .testTag("result_tile_shotok")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFA7F3D0))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "শতাংশ (ডেসিমাল)",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "১ শতাংশ = ৪৩৫.৬ বর্গফুট",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.5.sp
                    )
                }

                Text(
                    text = if (isValid) BengaliFormat.formatNumber(shotokVal, 3) else "—",
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Two columns grid for the rest with high contrast & crisp borders
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            UnitTile(
                unit = AreaUnit.KATHA,
                value = kathaVal,
                subLabel = if (kathaStandard == KathaStandard.STANDARD_720) "৭২০ বর্গফুট" else "১.৬৫ শতাংশ",
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
            UnitTile(
                unit = AreaUnit.BIGHA,
                value = bighaVal,
                subLabel = "২০ কাঠা",
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            UnitTile(
                unit = AreaUnit.ACRE,
                value = acreVal,
                subLabel = "১০০ শতাংশ",
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
            UnitTile(
                unit = AreaUnit.SQ_METER,
                value = sqmVal,
                subLabel = "১০.৭৬ বর্গফুট",
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            UnitTile(
                unit = AreaUnit.SQ_FEET,
                value = sqftVal,
                subLabel = "মোট বর্গফুট",
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
            UnitTile(
                unit = AreaUnit.SQ_HAT,
                value = sqhatVal,
                subLabel = "২.২৫ বর্গফুট",
                isValid = isValid,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun UnitTile(
    unit: AreaUnit,
    value: Double,
    subLabel: String,
    isValid: Boolean,
    modifier: Modifier = Modifier
) {
    val unitColor = Color(unit.hexColor)
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) {
        unitColor.copy(alpha = 0.14f)
    } else {
        unitColor.copy(alpha = 0.12f)
    }
    val borderColor = unitColor.copy(alpha = if (isDark) 0.42f else 0.28f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.5.dp, borderColor, RoundedCornerShape(16.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("result_tile_${unit.id}")
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .clip(CircleShape)
                            .background(unitColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = unit.titleBn,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = subLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 10.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = if (isValid) BengaliFormat.formatNumber(value, unit.decimals) else "—",
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
