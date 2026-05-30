package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkForest
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

data class TeamMember(
    val role: String,
    val name: String,
    val emoji: String,
    val backgroundInfo: String,
    val visionQuote: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeamPage(modifier: Modifier = Modifier) {
    val scrollState = rememberScrollState()

    // 7 Required C-suite roles strictly mapped to realistic student founders
    val team = listOf(
        TeamMember(
            "Chief Executive Officer (CEO)",
            "Javohir",
            "👨‍💼",
            "Global Economics major at Inha University. Leads strategic planning, university partnerships, sustainability initiatives, and business development.",
            "Making detergent refilling a normal part of student life in Korea."
        ),
        TeamMember(
            "Chief Technology Officer (CTO)",
            "Jahongir",
            "💻",
            "Software Engineering student at Inha University. Designed the RefillGo platform, mobile application architecture, QR workflow, mapping system, analytics dashboard, and technology roadmap.",
            "Building practical technology that turns sustainability into everyday action."
        ),
        TeamMember(
            "Chief Marketing Officer (CMO)",
            "Milana",
            "📣",
            "Responsible for branding, user acquisition, survey research, social media strategy, and student engagement campaigns.",
            "Making eco-friendly choices simple and attractive for students."
        ),
        TeamMember(
            "Chief Financial Officer (CFO)",
            "Begzod",
            "📊",
            "Leads pricing strategy, financial planning, revenue modeling, startup costs, and funding opportunities.",
            "Creating a sustainable business model that benefits both students and the environment."
        ),
        TeamMember(
            "Chief Operations Officer (COO)",
            "Mukhammadazam",
            "⚙️",
            "Responsible for kiosk operations, supply chain management, maintenance planning, and detergent logistics.",
            "Delivering a reliable refill experience across every campus."
        ),
        TeamMember(
            "Chief Information Officer (CIO)",
            "Sherzod",
            "🔬",
            "Conducts industry research, technology strategy planning, competitive analysis, sustainability regulation review, and innovation assessment.",
            "Using data to drive smarter environmental solutions."
        ),
        TeamMember(
            "Chief Human Resources Manager (CHRM)",
            "Sara",
            "👥",
            "Coordinates team management, organizational planning, staffing strategy, and community engagement.",
            "Building a mission-driven team that creates real impact."
        )
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP GENERAL BANNER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(PrimaryGreen.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Diversity3,
                    contentDescription = "team iconic",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "RefillGo Pioneers",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForest
                )
                Text(
                    text = "The university student founders behind the clean detergent revolution",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // FLOW GRID RENDER FOR TEAM CARDS
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 1
        ) {
            team.forEach { member ->
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Avatar emoji box
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(OffWhiteBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = member.emoji, fontSize = 28.sp)
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = member.name,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1F2937)
                                )
                                Text(
                                    text = member.role,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryGreen
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Background bio details
                        Text(
                            text = member.backgroundInfo,
                            fontSize = 12.sp,
                            color = Color.DarkGray,
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quote
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(PrimaryGreen.copy(alpha = 0.05f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FormatQuote,
                                contentDescription = "quote",
                                tint = PrimaryGreen.copy(alpha = 0.5f),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = member.visionQuote,
                                fontSize = 11.sp,
                                fontStyle = FontStyle.Italic,
                                color = DarkForest,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
