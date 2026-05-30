package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.viewmodel.UserViewModel
import com.example.data.RefillLog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfilePage(
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    val user = userViewModel.currentUser
    val refills by userViewModel.userRefills.collectAsState()
    var simLogMsg by remember { mutableStateOf("") }
    var isSimulating by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA)),
        contentPadding = PaddingValues(bottom = 90.dp) // Avoid overlap with nav bar
    ) {
        if (user == null) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PrimaryGreen)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading student profile...", color = TextMuted, fontSize = 14.sp)
                    }
                }
            }
        } else {
            // Part 6 — PROFILE HEADER
            item {
                ProfileHeader(
                    username = user.username,
                    phoneNumber = user.phoneNumber,
                    createdAt = user.createdAt,
                    onLogout = { userViewModel.logout() }
                )
            }

            // Part 7 — ENVIRONMENTAL IMPACT VISUALS
            item {
                EnvironmentalImpactDashboard(
                    totalRefills = user.totalRefills,
                    totalVolumeMl = user.totalVolumeRefilled,
                    bottlesSaved = user.totalBottlesSaved,
                    plasticSavedKg = user.totalPlasticSaved,
                    co2SavedKg = user.totalCO2Prevented
                )
            }

            // Part 10 — CLASSROOM DEMO SIMULATOR CONTROL (Inside Profile so professors / presentation hosts can invoke it)
            item {
                DemoSimulationControl(
                    simLogMsg = simLogMsg,
                    isSimulating = isSimulating,
                    onTriggerSimulation = {
                        isSimulating = true
                        simLogMsg = "Initializing high-concurrency simulation..."
                        userViewModel.triggerDemoUserGenerator { status ->
                            simLogMsg = status
                            isSimulating = false
                        }
                    }
                )
            }

            // Part 8 — REFILL TIMELINE TIMELINE
            item {
                Text(
                    text = "Refill Activity History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForest,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }

            if (refills.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = TextMuted.copy(alpha = 0.5f),
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "No refill logs on file yet.",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextMuted
                            )
                            Text(
                                "Use the QR code scanning flow to request an active faucet dispenser dispense.",
                                fontSize = 11.sp,
                                color = TextMuted.copy(alpha = 0.8f),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            } else {
                items(refills) { log ->
                    TimelineItemRow(log = log)
                }
            }
        }
    }
}

@Composable
fun ProfileHeader(
    username: String,
    phoneNumber: String,
    createdAt: Long,
    onLogout: () -> Unit
) {
    val dateStr = remember(createdAt) {
        val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
        sdf.format(Date(createdAt))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    colors = listOf(DarkForest, Color(0xFF0F5A47))
                )
            )
            .padding(top = 40.dp, start = 24.dp, end = 24.dp, bottom = 32.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Circular Avatar with Gradient Outline
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.15f))
                        .border(2.dp, Color(0xFF34D399), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = username,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        
                        // Pupil badge
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF10B981), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "STUDENT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = phoneNumber,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f),
                        fontWeight = FontWeight.Medium
                    )

                    Text(
                        text = "Member since: $dateStr",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.55f)
                    )
                }

                IconButton(
                    onClick = onLogout,
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.1f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun EnvironmentalImpactDashboard(
    totalRefills: Int,
    totalVolumeMl: Int,
    bottlesSaved: Int,
    plasticSavedKg: Double,
    co2SavedKg: Double
) {
    // Math equivalents for Part 7 Impact calculations
    val calculatedCashSaved = (totalVolumeMl / 1000.0) * 4500.0 // Active savings model: Average 4500 Won saved per Litre vs retail detergents

    // Part 6 Animated Counters
    val animatedRefills by animateIntAsState(targetValue = totalRefills, animationSpec = tween(1200))
    val animatedVolume by animateIntAsState(targetValue = totalVolumeMl, animationSpec = tween(1200))
    val animatedBottles by animateIntAsState(targetValue = bottlesSaved, animationSpec = tween(1200))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "My Environmental Impact",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = DarkForest,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Micro Metric Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.WaterDrop,
                        contentDescription = null,
                        tint = SecondaryBlue,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$animatedRefills",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForest
                    )
                    Text(
                        text = "Total Refills",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Card(
                modifier = Modifier.weight(1.3f),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, BorderLight),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Icon(
                        imageVector = Icons.Default.Eco,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${String.format(Locale.US, "%.1f", totalVolumeMl / 1000.0)} Liters",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = DarkForest
                    )
                    Text(
                        text = "Detergent Refilled",
                        fontSize = 11.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // High visual cards representing impact
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, BorderLight),
            shape = RoundedCornerShape(20.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                // Metric 1: Plastic Bottles Saved
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFE0F2FE), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF0284C7),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Plastic Prevented",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "You prevented $animatedBottles plastic bottles.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForest
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { minOf(1f, animatedBottles / 40f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = Color(0xFF0284C7),
                    trackColor = Color(0xFFF0F9FF)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Metric 2: Carbon Footprint Avoided
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFECFDF5), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Co2,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Footprint Reduced",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "You reduced ${String.format(Locale.US, "%.2f", co2SavedKg)}kg CO₂.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForest
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { minOf(1f, co2SavedKg.toFloat() / 15f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(CircleShape),
                    color = PrimaryGreen,
                    trackColor = Color(0xFFECFDF5)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Metric 3: Money Saved in KRW (Calculated locally vs buying new bottles)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFEF3C7), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Payments,
                            contentDescription = null,
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Economic Dividends",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                        Text(
                            text = "You saved ₩${String.format(Locale.US, "%,d", calculatedCashSaved.toInt())}.",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForest
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DemoSimulationControl(
    simLogMsg: String,
    isSimulating: Boolean,
    onTriggerSimulation: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEEF2F6)),
        border = BorderStroke(1.dp, Color(0xFFD0D7DE)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = DarkForest,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Classroom Presentation Simulator",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = DarkForest
                    )
                    Text(
                        text = "Builds heatmaps & transactions in Room Database.",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = onTriggerSimulation,
                colors = ButtonDefaults.buttonColors(containerColor = DarkForest),
                shape = RoundedCornerShape(8.dp),
                enabled = !isSimulating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSimulating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp))
                } else {
                    Text(
                        text = "Simulate Demo Users & Heatmaps",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (simLogMsg.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = simLogMsg,
                    fontSize = 11.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(6.dp))
                        .padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun TimelineItemRow(log: RefillLog) {
    val dateStr = remember(log.timestamp) {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.US)
        sdf.format(Date(log.timestamp))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, BorderLight),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocalGasStation,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = log.stationName,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForest
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Text(
                        text = "$dateStr",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(3.dp)
                            .background(BorderLight, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = log.paymentMethod,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextMuted
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${log.amountMl}ml",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = DarkForest
                )
                
                // Status Bubble
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .background(Color(0xFFD1FAE5), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = log.status,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46)
                    )
                }
            }
        }
    }
}
