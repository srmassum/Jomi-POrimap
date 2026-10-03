package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Terrain
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.model.LandShape
import com.example.model.LengthUnit
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandIndigoDark
import com.example.ui.theme.BrandIndigoMedium
import com.example.util.BengaliFormat
import com.example.util.LandMath

@Composable
fun HeroSection(
    activeTab: Int,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    shape: LandShape,
    unit: LengthUnit,
    values: Map<String, String>,
    nTriangles: Int,
    focusedKey: String?,
    totalSqFt: Double,
    heroDisplayUnit: AreaUnit,
    kathaStandard: KathaStandard,
    onSelectHeroUnit: (AreaUnit) -> Unit,
    modifier: Modifier = Modifier
) {
    var unitMenuExpanded by remember { mutableStateOf(false) }

    val convertedValue = if (totalSqFt > 0) {
        LandMath.convertSqFtTo(totalSqFt, heroDisplayUnit, kathaStandard)
    } else 0.0

    val formattedTotal = if (totalSqFt > 0) {
        BengaliFormat.formatNumber(convertedValue, heroDisplayUnit.decimals)
    } else "০"

    val animatedValue by animateFloatAsState(
        targetValue = if (totalSqFt > 0) convertedValue.toFloat() else 0f,
        animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing),
        label = "heroAreaAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
    ) {
        // Farmland Background Image
        Image(
            painter = painterResource(id = R.drawable.farmland_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            BrandIndigoDark.copy(alpha = 0.90f),
                            BrandIndigoMedium.copy(alpha = 0.82f),
                            BrandCyan.copy(alpha = 0.70f)
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = if (activeTab == 0) 80.dp else 40.dp
                )
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Color.White.copy(alpha = 0.20f))
                        .border(1.dp, Color.White.copy(alpha = 0.35f), RoundedCornerShape(13.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Terrain,
                        contentDescription = "App Logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "ভূমি পরিমাপ",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "জমির ক্ষেত্রফল ক্যালকুলেটর",
                        color = Color.White.copy(alpha = 0.90f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Dark/Light Theme Button
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.16f))
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "থিম পরিবর্তন করুন",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Land Stage (Shown on Calculator tab)
            if (activeTab == 0) {
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(22.dp))
                        .background(Color.White.copy(alpha = 0.14f))
                        .border(1.dp, Color.White.copy(alpha = 0.26f), RoundedCornerShape(22.dp))
                        .padding(horizontal = 14.dp, vertical = 12.dp)
                ) {
                    // SVG/Canvas Land Geometry
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(145.dp)
                    ) {
                        LandCanvas(
                            shape = shape,
                            unit = unit,
                            values = values,
                            nTriangles = nTriangles,
                            focusedKey = focusedKey,
                            modifier = Modifier.matchParentSize()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom readout with unit selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 0.5.dp,
                                color = Color.White.copy(alpha = 0.25f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "মোট ক্ষেত্রফল",
                                color = Color.White.copy(alpha = 0.90f),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            AnimatedContent(
                                targetState = formattedTotal,
                                transitionSpec = { fadeIn() togetherWith fadeOut() },
                                label = "totalNumberAnim"
                            ) { displayNum ->
                                val fontSize = if (displayNum.length > 11) 23.sp else if (displayNum.length > 8) 27.sp else 32.sp
                                Text(
                                    text = displayNum,
                                    color = Color.White,
                                    fontSize = fontSize,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        // Unit dropdown pill
                        Box {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(999.dp))
                                    .background(Color.White.copy(alpha = 0.22f))
                                    .border(1.2.dp, Color.White.copy(alpha = 0.50f), RoundedCornerShape(999.dp))
                                    .clickable { unitMenuExpanded = true }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("hero_unit_selector"),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = heroDisplayUnit.titleBn,
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = "একক নির্বাচন",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = unitMenuExpanded,
                                onDismissRequest = { unitMenuExpanded = false }
                            ) {
                                val units = listOf(
                                    AreaUnit.SQ_FEET,
                                    AreaUnit.SHOTOK,
                                    AreaUnit.KATHA,
                                    AreaUnit.BIGHA,
                                    AreaUnit.ACRE,
                                    AreaUnit.SQ_METER,
                                    AreaUnit.SQ_HAT
                                )
                                units.forEach { u ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                text = u.titleBn,
                                                fontWeight = if (u == heroDisplayUnit) FontWeight.Bold else FontWeight.Normal,
                                                color = if (u == heroDisplayUnit) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                            )
                                        },
                                        onClick = {
                                            onSelectHeroUnit(u)
                                            unitMenuExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
