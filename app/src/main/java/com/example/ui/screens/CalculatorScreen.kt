package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.model.LandShape
import com.example.model.LengthUnit
import com.example.ui.components.ResultTiles
import com.example.ui.theme.BrandIndigoMedium
import com.example.util.BengaliFormat
import com.example.util.LandMath

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalculatorScreen(
    shape: LandShape,
    unit: LengthUnit,
    values: Map<String, String>,
    nTriangles: Int,
    landName: String,
    totalSqFt: Double,
    kathaStandard: KathaStandard,
    warningMessage: String?,
    onSelectShape: (LandShape) -> Unit,
    onSelectUnit: (LengthUnit) -> Unit,
    onValueChange: (String, String) -> Unit,
    onFieldFocusChange: (String?) -> Unit,
    onLandNameChange: (String) -> Unit,
    onAddTriangle: () -> Unit,
    onRemoveTriangle: () -> Unit,
    onSave: () -> Unit,
    onShowMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isOppositeExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Shape Selection Chips (2 Lines: 3 in line 1, 2 in line 2)
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Line 1: আয়তক্ষেত্র, ত্রিভুজ, ট্রাপিজিয়াম
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val row1Shapes = listOf(LandShape.RECTANGLE, LandShape.TRIANGLE, LandShape.TRAPEZOID)
                row1Shapes.forEach { s ->
                    ShapeChipItem(
                        shape = s,
                        isSelected = s == shape,
                        onClick = { onSelectShape(s) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Line 2: বৃত্ত, অনিয়মিত (Centered 2-chip row)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val row2Shapes = listOf(LandShape.CIRCLE, LandShape.IRREGULAR)
                row2Shapes.forEach { s ->
                    ShapeChipItem(
                        shape = s,
                        isSelected = s == shape,
                        onClick = { onSelectShape(s) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Measurement Unit Segment
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "মাপের একক",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .padding(4.dp)
                    ) {
                        LengthUnit.entries.forEach { u ->
                            val isSelected = u == unit
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { onSelectUnit(u) }
                                    .padding(horizontal = 14.dp, vertical = 7.dp)
                                    .testTag("unit_segment_${u.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = u.titleBn,
                                    fontSize = 13.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                // Dynamic Input Fields
                when (shape) {
                    LandShape.RECTANGLE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MeasurementInputField(
                                label = "দৈর্ঘ্য (১ম পাশ)",
                                key = "l",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInputField(
                                label = "প্রস্থ (১ম পাশ)",
                                key = "w",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // Expandable for opposite sides
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { isOppositeExpanded = !isOppositeExpanded }
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "উল্টো পাশের মাপ কি আলাদা?",
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.5.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    imageVector = if (isOppositeExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }

                            AnimatedVisibility(
                                visible = isOppositeExpanded,
                                enter = expandVertically() + fadeIn(),
                                exit = shrinkVertically() + fadeOut()
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        MeasurementInputField(
                                            label = "উল্টো দিকের দৈর্ঘ্য",
                                            key = "l2",
                                            unitLabel = unit.shortBn,
                                            shapeId = shape.id,
                                            values = values,
                                            onValueChange = onValueChange,
                                            onFocusChange = onFieldFocusChange,
                                            modifier = Modifier.weight(1f)
                                        )
                                        MeasurementInputField(
                                            label = "উল্টো দিকের প্রস্থ",
                                            key = "w2",
                                            unitLabel = unit.shortBn,
                                            shapeId = shape.id,
                                            values = values,
                                            onValueChange = onValueChange,
                                            onFocusChange = onFieldFocusChange,
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                    Text(
                                        text = "ফাঁকা রাখলে ১ম পাশের মাপই ধরা হবে। আলাদা হলে গড় দৈর্ঘ্য × গড় প্রস্থ দিয়ে হিসাব হয়।",
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }

                    LandShape.TRIANGLE -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MeasurementInputField(
                                label = "বাহু a",
                                key = "a",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInputField(
                                label = "বাহু b",
                                key = "b",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInputField(
                                label = "বাহু c",
                                key = "c",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    LandShape.TRAPEZOID -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            MeasurementInputField(
                                label = "উপরের বাহু a",
                                key = "a",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInputField(
                                label = "নিচের বাহু b",
                                key = "b",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                            MeasurementInputField(
                                label = "উচ্চতা h",
                                key = "h",
                                unitLabel = unit.shortBn,
                                shapeId = shape.id,
                                values = values,
                                onValueChange = onValueChange,
                                onFocusChange = onFieldFocusChange,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    LandShape.CIRCLE -> {
                        MeasurementInputField(
                            label = "বৃত্তের ব্যাসার্ধ (Radius r)",
                            key = "r",
                            unitLabel = unit.shortBn,
                            shapeId = shape.id,
                            values = values,
                            onValueChange = onValueChange,
                            onFocusChange = onFieldFocusChange,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    LandShape.IRREGULAR -> {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            for (i in 1..nTriangles) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(10.dp)
                                ) {
                                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = "ত্রিভুজ ${BengaliFormat.toBengaliDigits(i.toString())}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            MeasurementInputField(
                                                label = "বাহু a",
                                                key = "${i}a",
                                                unitLabel = unit.shortBn,
                                                shapeId = shape.id,
                                                values = values,
                                                onValueChange = onValueChange,
                                                onFocusChange = onFieldFocusChange,
                                                modifier = Modifier.weight(1f)
                                            )
                                            MeasurementInputField(
                                                label = "বাহু b",
                                                key = "${i}b",
                                                unitLabel = unit.shortBn,
                                                shapeId = shape.id,
                                                values = values,
                                                onValueChange = onValueChange,
                                                onFocusChange = onFieldFocusChange,
                                                modifier = Modifier.weight(1f)
                                            )
                                            MeasurementInputField(
                                                label = "বাহু c",
                                                key = "${i}c",
                                                unitLabel = unit.shortBn,
                                                shapeId = shape.id,
                                                values = values,
                                                onValueChange = onValueChange,
                                                onFocusChange = onFieldFocusChange,
                                                modifier = Modifier.weight(1f)
                                            )
                                        }
                                    }
                                }
                            }

                            // Add / Remove Triangle Buttons
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                if (nTriangles < 5) {
                                    OutlinedButton(
                                        onClick = onAddTriangle,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("+ ত্রিভুজ যোগ")
                                    }
                                } else {
                                    Spacer(modifier = Modifier.width(1.dp))
                                }

                                if (nTriangles > 1) {
                                    OutlinedButton(
                                        onClick = onRemoveTriangle,
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("− বাদ দিন")
                                    }
                                }
                            }

                            Text(
                                text = "অনিয়মিত জমির ক্ষেত্রফল নিখুঁতভাবে মাপার জন্য জমিকে কয়েকটি ত্রিভুজে ভাগ করে প্রত্যেক ত্রিভুজের ৩টি বাহু মেপে নিন।",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Warning Banner
                if (!warningMessage.isNullOrBlank()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "সতর্কতা",
                            tint = Color(0xFFB45309),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = warningMessage,
                            color = Color(0xFF92400E),
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Result Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.2.dp, MaterialTheme.colorScheme.outline),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ফলাফল",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (totalSqFt > 0) "বিভিন্ন এককে" else "মাপ দিলে হিসাব দেখা যাবে",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // Result Tiles
                ResultTiles(
                    totalSqFt = totalSqFt,
                    kathaStandard = kathaStandard
                )

                // Land Name Field
                OutlinedTextField(
                    value = landName,
                    onValueChange = onLandNameChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("land_name_input"),
                    label = { Text("জমির নাম (ঐচ্ছিক)") },
                    placeholder = { Text("যেমন: বাড়ির পেছনের জমি / দাগ নং ১২") },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                    singleLine = true
                )

                // Actions: Save, Copy, Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onSave,
                        enabled = totalSqFt > 0,
                        modifier = Modifier
                            .weight(1.8f)
                            .testTag("save_button"),
                        shape = RoundedCornerShape(14.dp),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bookmark,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "হিসাব সংরক্ষণ করুন",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            if (totalSqFt > 0) {
                                val shotok = LandMath.convertSqFtTo(totalSqFt, AreaUnit.SHOTOK, kathaStandard)
                                val katha = LandMath.convertSqFtTo(totalSqFt, AreaUnit.KATHA, kathaStandard)
                                val bigha = LandMath.convertSqFtTo(totalSqFt, AreaUnit.BIGHA, kathaStandard)
                                val summary = "${shape.titleBn}: ${BengaliFormat.formatNumber(totalSqFt, 2)} বর্গফুট | ${BengaliFormat.formatNumber(shotok, 3)} শতাংশ | ${BengaliFormat.formatNumber(katha, 3)} কাঠা | ${BengaliFormat.formatNumber(bigha, 4)} বিঘা"
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("ভূমি পরিমাপ", summary)
                                clipboard.setPrimaryClip(clip)
                                onShowMessage("ফলাফল কপি হয়েছে")
                            }
                        },
                        enabled = totalSqFt > 0,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_button"),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "কপি",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কপি", fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = {
                            if (totalSqFt > 0) {
                                val shotok = LandMath.convertSqFtTo(totalSqFt, AreaUnit.SHOTOK, kathaStandard)
                                val katha = LandMath.convertSqFtTo(totalSqFt, AreaUnit.KATHA, kathaStandard)
                                val bigha = LandMath.convertSqFtTo(totalSqFt, AreaUnit.BIGHA, kathaStandard)
                                val summary = "ভূমি পরিমাপ — ${shape.titleBn}:\nমোট ক্ষেত্রফল: ${BengaliFormat.formatNumber(totalSqFt, 2)} বর্গফুট\nশতাংশ: ${BengaliFormat.formatNumber(shotok, 3)} শতাংশ\nকাঠা: ${BengaliFormat.formatNumber(katha, 3)} কাঠা\nবিঘা: ${BengaliFormat.formatNumber(bigha, 4)} বিঘা"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, summary)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "জমির পরিমাপ শেয়ার করুন")
                                context.startActivity(shareIntent)
                            }
                        },
                        enabled = totalSqFt > 0,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.5.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(14.dp))
                            .testTag("share_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "শেয়ার",
                            tint = if (totalSqFt > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
fun MeasurementInputField(
    label: String,
    key: String,
    unitLabel: String,
    shapeId: String,
    values: Map<String, String>,
    onValueChange: (String, String) -> Unit,
    onFocusChange: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val fullKey = "${shapeId}_$key"
    val rawValue = values[fullKey] ?: ""
    val focusManager = LocalFocusManager.current

    OutlinedTextField(
        value = rawValue,
        onValueChange = { newVal ->
            // Allow Bengali and English numbers and dot
            val sanitized = newVal.filter { it.isDigit() || it in '০'..'৯' || it == '.' || it == ',' }
            onValueChange(key, sanitized)
        },
        modifier = modifier
            .onFocusChanged { state ->
                if (state.isFocused) {
                    onFocusChange(key)
                } else {
                    onFocusChange(null)
                }
            }
            .testTag("input_${shapeId}_$key"),
        label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Bold) },
        textStyle = androidx.compose.ui.text.TextStyle(
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        ),
        trailingIcon = {
            Box(
                modifier = Modifier
                    .padding(end = 6.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                    .padding(horizontal = 7.dp, vertical = 4.dp)
            ) {
                Text(
                    text = unitLabel,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
        ),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Decimal,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { focusManager.moveFocus(FocusDirection.Down) },
            onDone = { focusManager.clearFocus() }
        ),
        singleLine = true
    )
}

private fun getShapeDrawableRes(shape: LandShape): Int {
    return when (shape) {
        LandShape.RECTANGLE -> R.drawable.ic_shape_rectangle
        LandShape.TRIANGLE -> R.drawable.ic_shape_triangle
        LandShape.TRAPEZOID -> R.drawable.ic_shape_trapezoid
        LandShape.CIRCLE -> R.drawable.ic_shape_circle
        LandShape.IRREGULAR -> R.drawable.ic_shape_irregular
    }
}

@Composable
private fun ShapeChipItem(
    shape: LandShape,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shapeColor = Color(shape.hexColor)
    val bgColor = if (isSelected) shapeColor else MaterialTheme.colorScheme.surface
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
    val iconColor = if (isSelected) Color.White else shapeColor
    val borderColor = if (isSelected) shapeColor else MaterialTheme.colorScheme.outline

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(bgColor)
            .border(
                width = if (isSelected) 2.dp else 1.5.dp,
                color = borderColor,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 11.dp)
            .testTag("shape_chip_${shape.id}"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        val iconRes = getShapeDrawableRes(shape)
        Icon(
            painter = painterResource(iconRes),
            contentDescription = shape.titleBn,
            tint = iconColor,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = shape.titleBn,
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 13.5.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

