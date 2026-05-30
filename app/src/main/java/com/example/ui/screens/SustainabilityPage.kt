package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Co2
import androidx.compose.material.icons.filled.EnergySavingsLeaf
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.RefillLog
import com.example.ui.components.DetergentRefillBarChart
import com.example.ui.components.WaterFootprintLineChart
import com.example.viewmodel.RefillViewModel
import com.example.ui.theme.DarkForest
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

@Composable
fun SustainabilityPage(
    viewModel: RefillViewModel,
    logs: List<RefillLog>,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Aggregate values derived from the Room database logs
    val totalRefills = logs.size
    val totalVolumeMl = logs.sumOf { it.amountMl }
    
    // Bottles saved calculation
    val baseBottlesSaved = 345
    val currentRefillBottles = (totalVolumeMl / 500.0)
    val bottlesSavedTotal = baseBottlesSaved + currentRefillBottles.toInt()

    // Plastic saved in grams (approx 45g per standard single bottle)
    val basePlasticSavedGrams = 15525 // ~15.5 kg
    val currentPlasticGrams = (currentRefillBottles * 45).toInt()
    val totalPlasticKg = (basePlasticSavedGrams + currentPlasticGrams) / 1000f

    // CO2 footprint reduced in Kg (Approx 240g of standard extraction emission per bottle)
    val baseCo2Grams = 82800 // ~82.8 kg
    val currentCo2Grams = (currentRefillBottles * 240).toInt()
    val totalCo2Kg = (baseCo2Grams + currentCo2Grams) / 1000f

    // 1. Chart Data Extraction
    // Aggregate volume by past 7 weeks or items to keep graph clean
    val chartDataList = remember(logs) {
        if (logs.isEmpty()) {
            listOf(0.4f, 0.6f, 0.8f, 0.5f, 0.9f)
        } else {
            // Take the last 6 refills represent weekly trend
            logs.take(6).reversed().map { it.amountMl.toFloat() / 1000f }
        }
    }

    // 2. Water Saving Analytics (Every refill saves substantial production water, e.g. 50L per plastic bottle synthesized)
    val waterDataList = remember(logs) {
        // Mocking a beautiful sinusoidal fluid line tracking trend
        listOf(120f, 180f, 150f, 220f, 290f, 240f, 310f, 380f + totalRefills * 12f)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // TOP HEADER TITLE
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Sustainability Lab",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = DarkForest
                )
                Text(
                    text = "Inha campus ecometric dashboard",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            IconButton(onClick = { viewModel.resetDatabase() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset database logs",
                    tint = Color.Gray
                )
            }
        }

        // GRID OF STATISTICS (ANIMATED COUNTERS EFFECT)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 1: Bottles Saved
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(PrimaryGreen.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.EnergySavingsLeaf,
                            contentDescription = "leaf",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "$bottlesSavedTotal",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = PrimaryGreen
                    )
                    Text(
                        text = "Plastic Bottles Saved",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Card 2: Plastic prevented (kg)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(SecondaryBlue.copy(alpha = 0.1f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "plastic drops",
                            tint = SecondaryBlue,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = String.format("%.2f kg", totalPlasticKg),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = SecondaryBlue
                    )
                    Text(
                        text = "Plastic Waste Avoided",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card 3: CO2 saved (kg)
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFFFEE58).copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Co2,
                            contentDescription = "carbon saved",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = String.format("%.1f kg", totalCo2Kg),
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD97706)
                    )
                    Text(
                        text = "CO2 Footprint Reduced",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Card 4: Total Refill Operations Code
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.weight(1f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(0xFFE2E8F0), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.QueryStats,
                            contentDescription = "stats Count",
                            tint = Color(0xFF475569),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "${baseBottlesSaved + totalRefills}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "Total Refill Count",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // CHART PANEL 1: HISTORICAL USAGE ANALYTICS (Detergent Refilled in Litres)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Detergent Delivery Trend (Liters)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2937)
                )
                Text(
                    text = "Volume refilled on past active dormitory transactions",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DetergentRefillBarChart(
                    data = chartDataList,
                    labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat"),
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
        }

        // CHART PANEL 2: WATER SAVING TRACKING DASHBOARD (Greywater/Virtual Water Mitigated)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Saved Virtual Water Footprint (Liters)",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E2937)
                )
                Text(
                    text = "Total synthesis and packaging water averted by reusing bottles",
                    fontSize = 11.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                WaterFootprintLineChart(
                    dataPoints = waterDataList,
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                )
            }
        }

        // INTERACTIVE IMPACT CALCULATOR (Drag Sliders, updates values instant)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkForest),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Calculate,
                        contentDescription = "eco calculator",
                        tint = AccentLime,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sustainability Impact Calculator",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                Text(
                    text = "Estimate your personal annual savings and physical plastic trash reduction by discarding commercial bottle detergents.",
                    fontSize = 11.sp,
                    color = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                )

                // Slider 1: Loads per month
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Loads of Wash / Month",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${viewModel.laundryLoadsPerMonth.toInt()} cycle(s)",
                        fontSize = 12.sp,
                        color = AccentLime,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Slider(
                    value = viewModel.laundryLoadsPerMonth,
                    onValueChange = { viewModel.laundryLoadsPerMonth = it },
                    valueRange = 1f..20f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = AccentLime,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                        thumbColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Slider 2: Retail price of commercial products
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Commercial Detergent Price / ml",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "₩${viewModel.detergentRetailPricePerMl.toInt()} / ml",
                        fontSize = 12.sp,
                        color = AccentLime,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Slider(
                    value = viewModel.detergentRetailPricePerMl,
                    onValueChange = { viewModel.detergentRetailPricePerMl = it },
                    valueRange = 5f..20f,
                    colors = SliderDefaults.colors(
                        activeTrackColor = AccentLime,
                        inactiveTrackColor = Color.White.copy(alpha = 0.2f),
                        thumbColor = Color.White
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Calculated Results Panel
                val mlPerYear = (viewModel.laundryLoadsPerMonth * 120 * 12).toInt() // Standard laundry dose ml
                val retailAnnualPrice = mlPerYear * viewModel.detergentRetailPricePerMl.toInt()
                val refillGoAnnualPrice = mlPerYear * 6 // ₩6/ml standard rate
                val annualCashSaved = maxOf(0, retailAnnualPrice - refillGoAnnualPrice)
                val annualBottlesAverted = (mlPerYear / 500f).toInt() // 500ml standard

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.08f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Annual Detergent Volume Used:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(text = "${String.format("%,d", mlPerYear)} ml", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Plastic Containers Saved / Year:", fontSize = 12.sp, color = AccentLime, fontWeight = FontWeight.Bold)
                            Text(text = "$annualBottlesAverted bottle(s)", fontSize = 12.sp, color = AccentLime, fontWeight = FontWeight.Black)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Commercial Retail Cost / Year:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(text = "₩${String.format("%,d", retailAnnualPrice)}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "RefillGo Bulk Cost / Year:", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text(text = "₩${String.format("%,d", refillGoAnnualPrice)}", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        
                        Spacer(modifier = Modifier.height(10.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color.White.copy(alpha = 0.15f)))
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Net Cash Kept:", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Text(text = "₩${String.format("%,d", annualCashSaved)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = AccentLime)
                        }
                    }
                }
            }
        }

        // AI FORECAST PREDICTION COMMAND CENTER (Connected to Gemini API)
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    Brush.linearGradient(listOf(PrimaryGreen, SecondaryBlue)),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "AI Command",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Kiosk Demand Forecast",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Powered by Google Gemini-3.5-Flash",
                                fontSize = 11.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    // Live badge status
                    when (val state = viewModel.aiPredictionState) {
                        is RefillViewModel.PredictionState.Success -> {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (state.isLive) PrimaryGreen.copy(alpha = 0.12f) else Color(0xFFF3F4F6)
                                )
                            ) {
                                Text(
                                    text = if (state.isLive) "LIVE AI" else "OFFLINE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isLive) PrimaryGreen else Color.DarkGray,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                        else -> {}
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Runs neural analytics over dormitory refill logs to predict peak capacity volumes and optimize tank supply routes.",
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // TRIGGER VIEW PORT CONTAINER
                when (val status = viewModel.aiPredictionState) {
                    is RefillViewModel.PredictionState.Idle -> {
                        Button(
                            onClick = { viewModel.runAiDemandPrediction() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "run prediction",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Perform Campus Demand Forecast",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    is RefillViewModel.PredictionState.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Analyzing laundry refill records...",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                    is RefillViewModel.PredictionState.Success -> {
                        Column {
                            // Sleek retro command console displaying raw forecast output text
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .padding(16.dp)
                            ) {
                                Text(
                                    text = status.predictionText,
                                    color = Color(0xFFF1F5F9),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    lineHeight = 16.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Action button button to re-trigger
                            Button(
                                onClick = { viewModel.runAiDemandPrediction() },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF3F4F6),
                                    contentColor = Color.DarkGray
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "refresh ai",
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Recalculate Prediction Report",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    is RefillViewModel.PredictionState.Error -> {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2))
                        ) {
                            Text(
                                text = "Forecasting calculation took too long: ${status.errorMessage}",
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

private val AccentLime = Color(0xFF84CC16)
