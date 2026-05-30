package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkForest
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

data class RoadmapPhase(
    val phaseNumber: String,
    val title: String,
    val timeline: String,
    val description: String,
    val icon: @Composable () -> Unit,
    val status: String // "Completed", "Active", "Planned"
)

@Composable
fun ExpansionPage(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    val phases = listOf(
        RoadmapPhase(
            "Phase 1",
            "Inha University Pilot Study",
            "Active - Since Q1 2026",
            "Successfully installed 4 smart IoT detergent dispensers inside Wooryong A/B, Biryong, and Global structures, mitigating 12,504+ packaging bottles.",
            { Icon(Icons.Default.School, contentDescription = "School", tint = Color.White) },
            "Completed"
        ),
        RoadmapPhase(
            "Phase 2",
            "Yonsei University Expansion",
            "Target - Q4 2026",
            "Deploying 8 high-capacity dispenser nozzles in active Yonsei residential suites, expanding campus awareness and partnering directly with regional clean-laundry student councils.",
            { Icon(Icons.Default.Map, contentDescription = "Yonsei", tint = Color.White) },
            "Active"
        ),
        RoadmapPhase(
            "Phase 3",
            "Korea University Integration",
            "Target - Q2 2027",
            "Installing 12 smart bulk kiosks. Promoting organic bio-degradable detergent formulas in dorm laundries and securing municipal clean-tech grants.",
            { Icon(Icons.Default.CompassCalibration, contentDescription = "Korea", tint = Color.White) },
            "Planned"
        ),
        RoadmapPhase(
            "Phase 4",
            "National Expansion",
            "Target - Q1 2028",
            "Aggregating full municipal and industrial laundry lines, creating smart partnerships across 400+ South Korean campuses and launching retail bulk refill hubs nationwide.",
            { Icon(Icons.Default.Flag, contentDescription = "National", tint = Color.White) },
            "Planned"
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // TOP GENERAL BANNER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(PrimaryGreen.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Timeline,
                    contentDescription = "timeline",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "RefillGo Roadmap",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForest
                )
                Text(
                    text = "Scaling campus sustainability from Inha to the nation",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // RENDERING ROADMAP TIMELINE CARDS
        phases.forEachIndexed { index, ph ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Vertical Timeline graphics
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(50.dp)
                ) {
                    val connectorColor = when (ph.status) {
                        "Completed" -> PrimaryGreen
                        "Active" -> SecondaryBlue
                        else -> Color(0xFFD1D5DB)
                    }

                    // Globe pin dot
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(connectorColor, CircleShape)
                            .border(4.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        ph.icon()
                    }

                    // Connected line down (if not the last element)
                    if (index < phases.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(3.dp)
                                .height(120.dp)
                                .background(connectorColor)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Phase information Card
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = ph.phaseNumber.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (ph.status == "Completed") PrimaryGreen else SecondaryBlue,
                                letterSpacing = 1.sp
                            )

                            // Status badge
                            val badgeColor = when (ph.status) {
                                "Completed" -> Color(0xFFD1FAE5)
                                "Active" -> Color(0xFFE0F2FE)
                                else -> Color(0xFFF3F4F6)
                            }
                            val badgeText = when (ph.status) {
                                "Completed" -> PrimaryGreen
                                "Active" -> SecondaryBlue
                                else -> Color.Gray
                            }
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = badgeColor)
                            ) {
                                Text(
                                    text = ph.status,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeText,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                )
                            }
                        }

                        Text(
                            text = ph.title,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(top = 4.dp)
                        )

                        Text(
                            text = ph.timeline,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 2.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = ph.description,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
