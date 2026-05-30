package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvestorPage(
    isDemoRunning: Boolean,
    demoStatusText: String,
    onStartDemo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedSubTab by remember { mutableStateOf(0) } // 0 = Impact, 1 = Smart Kiosk, 2 = Appendix Gallery

    // Setup animated triggers for statistics when tab is loaded
    var triggerStatsAnimation by remember { mutableStateOf(false) }
    LaunchedEffect(selectedSubTab) {
        if (selectedSubTab == 0) {
            triggerStatsAnimation = false
            delay(100)
            triggerStatsAnimation = true
        }
    }

    // Dynamic stats animating values mapping to business proposals
    val targetPlasticAnnual = 110400
    val targetCo2Annual = 10488
    val targetSavingsAnnual = 66240000
    val targetLitresAnnual = 55200

    val animPlastic by animateIntAsState(
        targetValue = if (triggerStatsAnimation) targetPlasticAnnual else 0,
        animationSpec = tween(1200, easing = EaseOutExpo),
        label = "plastic"
    )
    val animCo2 by animateIntAsState(
        targetValue = if (triggerStatsAnimation) targetCo2Annual else 0,
        animationSpec = tween(1200, easing = EaseOutExpo),
        label = "co2"
    )
    val animSavings by animateIntAsState(
        targetValue = if (triggerStatsAnimation) targetSavingsAnnual else 0,
        animationSpec = tween(1500, easing = EaseOutQuart),
        label = "savings"
    )
    val animLitres by animateIntAsState(
        targetValue = if (triggerStatsAnimation) targetLitresAnnual else 0,
        animationSpec = tween(1300, easing = EaseOutExpo),
        label = "litres"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // 1. HEADER TITLE BANNER
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp, horizontal = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(PrimaryGreen.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Investor Portal",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Startup Pitch Desk",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForest
                )
                Text(
                    text = "RefillGo Inha Pilot Valuation Dashboard",
                    fontSize = 11.sp,
                    color = TextMuted,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. INVESTOR DEMO RUNNER MODULE
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, if (isDemoRunning) PrimaryGreen else BorderLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "STARTUP PITCH PRESENTATION",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Self-Guiding Pitch Mode",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = DarkForest
                        )
                    }

                    if (isDemoRunning) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFDCFCE7), RoundedCornerShape(12.dp))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(Color(0xFF16A34A), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "RUNNING",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF15803D)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "For academic juries and VC analysts, press the button below to trigger an automated self-guided walk of the full RefillGo product lifecycle in dynamic sequence.",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (isDemoRunning) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = OffWhiteBg),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = PrimaryGreen
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Automated Pitch Progression",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForest
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = demoStatusText,
                                fontSize = 11.sp,
                                color = TextDark,
                                fontStyle = FontStyle.Italic
                            )
                        }
                    }
                } else {
                    Button(
                        onClick = onStartDemo,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DarkForest),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = "Play presentation",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Start Investor Demo",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // 3. TAB CONTROLLERS
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf("Projection", "IoT Kiosk", "Gallery")
            tabs.forEachIndexed { idx, label ->
                val isSelected = selectedSubTab == idx
                Card(
                    onClick = { selectedSubTab = idx },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) PrimaryGreen else Color.White
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 2.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isSelected) Color.White else TextDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        // 4. SUB-TAB VIEWPORT
        when (selectedSubTab) {
            0 -> {
                // CAMPUS ANNUAL ENVIRONMENTAL PROJECTIONS (TAB 0)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "12-Month Inha Pilot Projections",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkForest,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Card 1: Annual Plastic Bottles Prevented
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFFECFDF5), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Plastic",
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = String.format("Annualized: %,d bottles", animPlastic),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Annual Plastic Bottles Prevented",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Card 2: Annual CO₂ Reduction
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFFEFF6FF), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Carbon footprint",
                                    tint = SecondaryBlue,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = String.format("%,d kg CO₂ averted", animCo2),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Annual CO₂ Emissions Reduced",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Card 3: Annual Student Financial Savings (₩66,240,000)
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFFFEF3C7), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Student Savings",
                                    tint = Color(0xFFD97706),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = String.format("₩%,d saved", animSavings),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Valuable student financial savings",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Card 4: Annual Detergent Refilled
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(Color(0xFFFEE2E2), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Detergent Volume",
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = String.format("%,d Litres dispensed", animLitres),
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Annual organic BioEster detergent",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Business Valuation highlights
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.05f)),
                        border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.15f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "University Economics Thesis",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "By eradicating single-use containers, RefillGo returns 40% value savings directly back to dormitory residents, establishing a scalable, repeatable loop across college compounds.",
                                fontSize = 11.sp,
                                color = TextDark,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
            1 -> {
                // REFILLGO SMART KIOSK METALLIC SCHEMATIC (TAB 1)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "RefillGo Smart IoT Kiosk Design",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkForest,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // 3D Metallic Slate Kiosk Outer Enclosure
                    Card(
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)), // Deep Obsidian Steel
                        border = BorderStroke(2.dp, Color(0xFF334155)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(12.dp, RoundedCornerShape(28.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 1. Branding Header
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .background(Color(0xFF10B981), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EnergySavingsLeaf,
                                            contentDescription = "IoT",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "RefillGo Kiosk V1.1",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(Color(0xFF334155), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "Inha Pilot ID: 04",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 2. High-Fidelity Transparent Fluid Tank
                            Text(
                                text = "TRANSPARENT BULK LIQUID CONTAINER",
                                fontSize = 8.sp,
                                color = Color(0xFF94A3B8),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(Alignment.Start)
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color(0xFF1E293B))
                                    .border(1.5.dp, Color(0xFF475569), RoundedCornerShape(16.dp))
                            ) {
                                // Dynamic Fluid draw utilizing Canvas
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val wavePath = Path()
                                    val waveHeight = 24.dp.toPx()
                                    val fillLevel = 45.dp.toPx() // Simulated liquid level height

                                    wavePath.moveTo(0f, size.height)
                                    wavePath.lineTo(0f, fillLevel)
                                    
                                    // Generate beautiful sine wave for liquid motion
                                    wavePath.cubicTo(
                                        size.width * 0.25f, fillLevel - waveHeight,
                                        size.width * 0.75f, fillLevel + waveHeight,
                                        size.width, fillLevel
                                    )
                                    wavePath.lineTo(size.width, size.height)
                                    wavePath.close()

                                    drawPath(
                                        path = wavePath,
                                        brush = Brush.verticalGradient(
                                            colors = listOf(Color(0xFF34D399), Color(0xFF047857))
                                        )
                                    )
                                }

                                // Fluid level indicator overlays
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(14.dp),
                                    verticalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "CAPACITY: 50.0 Litres",
                                            color = Color(0xFFF1F5F9),
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Text(
                                            text = "STATUS: STABLE",
                                            color = Color(0xFF34D399),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(30.dp))

                                    Column(modifier = Modifier.align(Alignment.End)) {
                                        Text(
                                            text = "Active: 31.4L",
                                            color = Color.White,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black
                                        )
                                        Text(
                                            text = "Eco-Sud Biodegradable Clean",
                                            color = Color.White.copy(alpha = 0.85f),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // 3. Simulated Digital Touchscreen and Interface
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Screen Area
                                Card(
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(140.dp)
                                        .border(1.5.dp, Color(0xFF38BDF8), RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFF020617))
                                ) {
                                    Column(
                                        modifier = Modifier.padding(10.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "REFILL INTERFACE",
                                                color = Color(0xFF38BDF8),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .size(6.dp)
                                                    .background(Color.Red, CircleShape)
                                            )
                                        }

                                        Divider(color = Color(0xFF1E293B))

                                        Text(
                                            text = "Select Dose Weight:",
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )

                                        Row(
                                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF0369A1), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("200ml", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF0284C7), RoundedCornerShape(4.dp))
                                                    .border(1.dp, Color(0xFF38BDF8), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text("500ml", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFF0369A1), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                                            ) {
                                                Text("1000ml", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(2.dp))

                                        Text(
                                            text = "Price: ₩3,000",
                                            color = Color(0xFF34D399),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                    }
                                }

                                // Secondary Panel: QR Scan Area + Refill Nozzle
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    // QR Scanner Panel Box
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QrCodeScanner,
                                                contentDescription = "QR area",
                                                tint = Color.White,
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "QR SCAN AREA",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 8.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }

                                    // Sealed Dispensing Nozzle Box
                                    Card(
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.WaterDrop,
                                                contentDescription = "Dispensing spout",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(20.dp)
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Text(
                                                text = "HYGIENE NOZZLE",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            2 -> {
                // PHOTO GALLERY PORTFOLIO (TAB 2)
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Venture Appendix Gallery",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = DarkForest,
                        modifier = Modifier.padding(start = 4.dp)
                    )

                    // Card Photo 1: Physical IoT Hardware
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1581091226825-a6a2a5aee158?auto=format&fit=crop&q=80&w=400",
                                contentDescription = "IoT nozzle control unit",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Automated Fluid Valve (Kiosk Interior)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Our microservice controls gravity dispensing using precision digital solenoid relays, avoiding chemical drips.",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Card Photo 2: Team Pioneers representation
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1522071820081-009f0129c71c?auto=format&fit=crop&q=80&w=400",
                                contentDescription = "Inha university team discussion",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Startup Pitch Delegation Meeting",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Inha University delegation comprising CEO Javohir, CTO Jahongir, and officers reviewing the campus eco-system pilot program.",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    // Card Photo 3: Modern detergent bulk refill hub
                    Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, BorderLight),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            AsyncImage(
                                model = "https://images.unsplash.com/photo-1607344645866-009c320c5ab8?auto=format&fit=crop&q=80&w=400",
                                contentDescription = "Detergent containers",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(160.dp),
                                contentScale = ContentScale.Crop
                            )
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "Municipal Bulk Liquid Chemical Supply Chain",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkForest
                                )
                                Text(
                                    text = "Strategic logistics networks to replenish bulk bio-friendly liquids safely without single-use plastic waste.",
                                    fontSize = 11.sp,
                                    color = TextMuted,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
