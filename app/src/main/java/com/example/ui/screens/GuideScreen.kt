package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AreaUnit
import com.example.model.KathaStandard
import com.example.util.BengaliFormat
import com.example.util.LandMath

@Composable
fun GuideScreen(
    currentStandard: KathaStandard,
    onSelectStandard: (KathaStandard) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Katha Setting Card
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
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "কাঠার মান নির্ধারণ",
                        fontSize = 16.sp,
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
                        KathaStandard.entries.forEach { std ->
                            val isSelected = std == currentStandard
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                                    )
                                    .clickable { onSelectStandard(std) }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                                    .testTag("katha_std_${std.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = std.titleBn,
                                    fontSize = 12.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }

                Text(
                    text = if (currentStandard == KathaStandard.STANDARD_720) {
                        "মানক নিয়ম অনুযায়ী ১ কাঠা = ৭২০ বর্গফুট (প্রায় ১.৬৫২৮ শতাংশ) এবং ১ বিঘা = ২০ কাঠা = ১৪,৪০০ বর্গফুট (৩৩.০৫ শতাংশ)।"
                    } else {
                        "১.৬৫ শতাংশ হিসাব ধরলে ১ কাঠা = ৭১৮.৭৪ বর্গফুট এবং ১ বিঘা = ৩৩ শতাংশ = ১৪,৩৭৪.৮ বর্গফুট হয়। আপনার দলিলের শর্ত অনুযায়ী মান নির্বাচন করুন।"
                    },
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        // Units Reference Guide Card
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
                Text(
                    text = "এককের নির্দেশিকা (ভূমি পরিমাপ)",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val guideItems = listOf(
                    Triple(
                        "১ শতাংশ (শতক / ডেসিমাল)",
                        "৪৩৫.৬ বর্গফুট = ৪০.৪৬ বর্গমিটার = ১০০০ বর্গ লিংক",
                        AreaUnit.SHOTOK
                    ),
                    Triple(
                        "১ কাঠা",
                        if (currentStandard == KathaStandard.STANDARD_720) "৭২০ বর্গফুট = ১.৬৫৩ শতাংশ = ১৬ ছটাক"
                        else "৭১৮.৭৪ বর্গফুট = ১.৬৫ শতাংশ",
                        AreaUnit.KATHA
                    ),
                    Triple(
                        "১ বিঘা",
                        if (currentStandard == KathaStandard.STANDARD_720) "২০ কাঠা = ১৪,৪০০ বর্গফুট = ৩৩.০৫৭ শতাংশ"
                        else "২০ কাঠা = ১৪,৩৭৪.৮ বর্গফুট = ৩৩ শতাংশ",
                        AreaUnit.BIGHA
                    ),
                    Triple(
                        "১ একর",
                        "১০০ শতাংশ = ৪৩,৫৬০ বর্গফুট = ৩ বিঘা ৮ ছটাক",
                        AreaUnit.ACRE
                    ),
                    Triple(
                        "১ বর্গমিটার",
                        "১০.৭৬৪ বর্গফুট = ১.১৯৬ বর্গগজ",
                        AreaUnit.SQ_METER
                    ),
                    Triple(
                        "১ হাত (রৈখিক)",
                        "১৮ ইঞ্চি = ১.৫ ফুট",
                        AreaUnit.SQ_HAT
                    ),
                    Triple(
                        "১ বর্গহাত",
                        "২.২৫ বর্গফুট (১ হাত × ১ হাত)",
                        AreaUnit.SQ_HAT
                    ),
                    Triple(
                        "গান্টার্স চেইন / শিকল",
                        "১ চেইন = ৬৬ ফুট = ১০০ লিংক (১ লিংক = ৭.৯২ ইঞ্চি)",
                        AreaUnit.SQ_FEET
                    ),
                    Triple(
                        "১ কানি (আঞ্চলিক)",
                        "সাধারণত ১৬০ শতাংশ (স্থানভেদে ৪০ শতাংশ বা কাচ্চা কানি)",
                        AreaUnit.SHOTOK
                    ),
                    Triple(
                        "১ গন্ডা (আঞ্চলিক)",
                        "৪ কড়া = ২ শতাংশ = ৮৭.১২ বর্গফুট",
                        AreaUnit.KATHA
                    )
                )

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    guideItems.forEach { (name, desc, unit) ->
                        val unitColor = Color(unit.hexColor)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .border(1.2.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(unitColor)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = desc,
                                    fontSize = 12.5.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                Text(
                    text = "বিঃদ্রঃ বিভিন্ন অঞ্চলে আঞ্চলিক একক ও হিস্যায় সামান্য তফাত হতে পারে। অফিসিয়াল দলিলের জন্য স্থানীয় ভূমি অফিসের রেকর্ড যাচাই করুন।",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Surveyor Advice Card
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
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "আমিন বা সার্ভেয়ারের পরামর্শ",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Text(
                    text = "• যদি জমির চারটি বাহুর মাপ সমান বা সমান্তরাল না হয়, তবে সরাসরি গড় দৈর্ঘ্য ও গড় প্রস্থ দিয়ে গুণ করলে বেশি ফল বের হতে পারে।\n\n• সবচেয়ে নির্ভরযোগ্য উপায় হলো যেকোনো এক কোণ থেকে উল্টো কোণে আড়াআড়ি (কর্ণ) ফিতা টেনে জমিকে দুটি বা ততোধিক ত্রিভুজে ভাগ করে নিয়ে হেরনের সূত্র (অনিয়মিত অপশন) ব্যবহার করা।",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}
