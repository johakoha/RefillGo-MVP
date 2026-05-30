package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.LocalMall
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.CampusStation
import com.example.viewmodel.RefillStep
import com.example.ui.theme.DarkForest
import com.example.ui.theme.OffWhiteBg
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

@Composable
fun RefillFlowPage(
    station: CampusStation?,
    activeStep: RefillStep,
    selectedAmountMl: Int,
    isDispensing: Boolean,
    dispenseProgress: Float,
    onAmountSelected: (Int) -> Unit,
    onStepTransition: (RefillStep) -> Unit,
    onTriggerDispense: (onDone: () -> Unit) -> Unit,
    onNavigateToDashboard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var detergentProduct by remember { mutableStateOf("Eco-Sud Organic Green") }
    var paymentMethodSelected by remember { mutableStateOf("NaverPay") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(OffWhiteBg)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Active Station Tag
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = PrimaryGreen.copy(alpha = 0.12f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocalLaundryService,
                    contentDescription = "Active Laundry Station info",
                    tint = PrimaryGreen,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Refill Node is connected: ${station?.name ?: "Wooryong Hall B"}",
                    color = DarkForest,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Step Segment Controller Container
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Progress Flow Indicator
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val stepsList = listOf("Choose", "Calculate", "Pay", "Dispense")
                    val activeIndex = when (activeStep) {
                        RefillStep.SELECT_AMOUNT -> 0
                        RefillStep.PRICE_CALC -> 1
                        RefillStep.QR_PAY -> 2
                        RefillStep.SUCCESS_DISPENSE -> 3
                    }

                    stepsList.forEachIndexed { idx, label ->
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (idx <= activeIndex) PrimaryGreen else Color(0xFFE5E7EB)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (idx == activeIndex) FontWeight.Bold else FontWeight.Normal,
                                color = if (idx == activeIndex) PrimaryGreen else Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                        if (idx < stepsList.size - 1) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(2.dp)
                                    .padding(horizontal = 4.dp)
                                    .background(
                                        if (idx < activeIndex) PrimaryGreen else Color(0xFFE5E7EB)
                                    )
                            )
                        }
                    }
                }

                // STEP DETAIL SWITCHER AREA
                AnimatedContent(
                    targetState = activeStep,
                    transitionSpec = {
                        fadeIn() togetherWith fadeOut()
                    },
                    label = "flow_step"
                ) { step ->
                    when (step) {
                        RefillStep.SELECT_AMOUNT -> {
                            Column {
                                Text(
                                    text = "Step 1: Select Detergent & Amount",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Liquid formulation selection
                                Text(
                                    text = "Detergent Liquid Choice",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val products = listOf("Eco-Sud Organic Green", "Pure-Dish Lemon Clean")
                                    products.forEach { p ->
                                        val isProdSelected = p == detergentProduct
                                        Card(
                                            onClick = { detergentProduct = p },
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isProdSelected) PrimaryGreen.copy(alpha = 0.08f) else Color(0xFFF9FAFB)
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(
                                                    1.dp,
                                                    if (isProdSelected) PrimaryGreen else Color(0xFFE5E7EB),
                                                    RoundedCornerShape(12.dp)
                                                )
                                        ) {
                                            Text(
                                                text = p,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                textAlign = TextAlign.Center,
                                                color = if (isProdSelected) PrimaryGreen else Color.DarkGray,
                                                modifier = Modifier.padding(12.dp).fillMaxWidth()
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Detergent Volume Selector Cards
                                Text(
                                    text = "Select Refill Volume",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                val amounts = listOf(
                                    Triple(200, "200ml", "Quick Cycle (₩1,200)"),
                                    Triple(500, "500ml", "Standard Choice (₩3,000)"),
                                    Triple(1000, "1 Liter", "Bulk Value (₩5,500)")
                                )

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    amounts.forEach { item ->
                                        val isAmtSelected = item.first == selectedAmountMl
                                        Card(
                                            onClick = { onAmountSelected(item.first) },
                                            shape = RoundedCornerShape(16.dp),
                                            colors = CardDefaults.cardColors(
                                                containerColor = if (isAmtSelected) PrimaryGreen.copy(alpha = 0.12f) else Color(0xFFF9FAFB)
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .border(
                                                    2.dp,
                                                    if (isAmtSelected) PrimaryGreen else Color.Transparent,
                                                    RoundedCornerShape(16.dp)
                                                )
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(12.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.WaterDrop,
                                                    contentDescription = "Drop Volume icon",
                                                    tint = if (isAmtSelected) PrimaryGreen else Color.LightGray,
                                                    modifier = Modifier.size(24.dp)
                                                )
                                                Text(
                                                    text = item.second,
                                                    fontSize = 14.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color(0xFF1E293B),
                                                    modifier = Modifier.padding(top = 4.dp)
                                                )
                                                Text(
                                                    text = item.third,
                                                    fontSize = 9.sp,
                                                    color = Color.Gray,
                                                    textAlign = TextAlign.Center,
                                                    lineHeight = 11.sp,
                                                    modifier = Modifier.padding(top = 2.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { onStepTransition(RefillStep.PRICE_CALC) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text(text = "Proceed to Pricing Calculation", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RefillStep.PRICE_CALC -> {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { onStepTransition(RefillStep.SELECT_AMOUNT) }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "back")
                                    }
                                    Text(
                                        text = "Step 2: Price Calculation",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                // Worksheet receipt breakdown
                                Card(
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                    modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(16.dp))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Product Selected:", fontSize = 13.sp, color = Color.Gray)
                                            Text(text = detergentProduct, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Dispensable Volume:", fontSize = 13.sp, color = Color.Gray)
                                            Text(text = "$selectedAmountMl ml", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(text = "Base Pricing ml (₩6/ml):", fontSize = 13.sp, color = Color.Gray)
                                            val basePrice = when (selectedAmountMl) {
                                                200 -> 1200
                                                500 -> 3000
                                                1000 -> 6000
                                                else -> selectedAmountMl * 6
                                            }
                                            Text(text = "₩$basePrice", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        
                                        // Dorm discount promo
                                        val finalPrice = when (selectedAmountMl) {
                                            200 -> 1200
                                            500 -> 3000
                                            1000 -> 5500 // ₩500 bundle savings discount applied!
                                            else -> selectedAmountMl * 6
                                        }
                                        val discount = when (selectedAmountMl) {
                                            1000 -> 500
                                            else -> 0
                                        }

                                        if (discount > 0) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = "Bulk Bundle Discount:", fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                                                Text(text = "-₩$discount", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                            }
                                        }
                                        
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Color(0xFFE5E7EB)))
                                        Spacer(modifier = Modifier.height(12.dp))

                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = "Net Amount Due:", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                                            Text(text = "₩$finalPrice", fontSize = 20.sp, fontWeight = FontWeight.Black, color = PrimaryGreen)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Select Payment Method
                                Text(
                                    text = "Choose Payment Gateway",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(bottom = 6.dp)
                                )

                                val payChannels = listOf("NaverPay", "KakaoPay", "TossPay", "Credit Card")
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    payChannels.forEach { cardName ->
                                        val isPaySelected = cardName == paymentMethodSelected
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(
                                                    if (isPaySelected) SecondaryBlue else Color(0xFFF3F4F6)
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isPaySelected) SecondaryBlue else Color(0xFFE5E7EB),
                                                    RoundedCornerShape(8.dp)
                                                )
                                                .clickable { paymentMethodSelected = cardName }
                                                .padding(vertical = 10.dp, horizontal = 2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = cardName,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isPaySelected) Color.White else Color.DarkGray
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(20.dp))

                                Button(
                                    onClick = { onStepTransition(RefillStep.QR_PAY) },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text(text = "Generate Payment QR", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RefillStep.QR_PAY -> {
                            val activeFinalPrice = when (selectedAmountMl) {
                                200 -> 1200
                                500 -> 3000
                                1000 -> 5500
                                else -> selectedAmountMl * 6
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { onStepTransition(RefillStep.PRICE_CALC) }) {
                                        Icon(Icons.Default.ArrowBack, contentDescription = "back")
                                    }
                                    Text(
                                        text = "Step 3: Secure Mobile Payment",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = "Pay ₩$activeFinalPrice via $paymentMethodSelected",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.DarkGray
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Simulated QR code render
                                Card(
                                    shape = RoundedCornerShape(24.dp),
                                    colors = CardDefaults.cardColors(containerColor = Color.White),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                    modifier = Modifier.size(200.dp).border(2.dp, SecondaryBlue, RoundedCornerShape(24.dp))
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize().padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        // Draw a detailed mockup QR container using native Canvas
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val w = size.width
                                            val h = size.height
                                            
                                            // Draw simulated QR matrix block corners
                                            val blockSize = 35f
                                            val colorQR = Color(0xFF0F172A)
                                            
                                            // Top Left corner
                                            drawRect(colorQR, Offset(0f, 0f), Size(blockSize, blockSize))
                                            drawRect(Color.White, Offset(8f, 8f), Size(blockSize - 16f, blockSize - 16f))
                                            drawRect(colorQR, Offset(12f, 12f), Size(blockSize - 24f, blockSize - 24f))
                                            
                                            // Top Right corner
                                            drawRect(colorQR, Offset(w - blockSize, 0f), Size(blockSize, blockSize))
                                            drawRect(Color.White, Offset(w - blockSize + 8f, 8f), Size(blockSize - 16f, blockSize - 16f))
                                            drawRect(colorQR, Offset(w - blockSize + 12f, 12f), Size(blockSize - 24f, blockSize - 24f))
                                            
                                            // Bottom Left corner
                                            drawRect(colorQR, Offset(0f, h - blockSize), Size(blockSize, blockSize))
                                            drawRect(Color.White, Offset(8f, h - blockSize + 8f), Size(blockSize - 16f, blockSize - 16f))
                                            drawRect(colorQR, Offset(12f, h - blockSize + 12f), Size(blockSize - 24f, blockSize - 24f))

                                            // Draw decorative inner QR bits random lines
                                            drawLine(colorQR, Offset(70f, 15f), Offset(105f, 15f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(80f, 25f), Offset(130f, 25f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(120f, 50f), Offset(120f, 90f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(45f, 80f), Offset(120f, 80f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(15f, 110f), Offset(85f, 110f), strokeWidth = 8f)
                                            
                                            drawLine(colorQR, Offset(110f, 110f), Offset(w - 15f, 110f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(110f, 120f), Offset(110f, 150f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(w - 45f, 130f), Offset(w - 15f, 130f), strokeWidth = 8f)
                                            drawLine(colorQR, Offset(75f, 145f), Offset(w - 45f, 145f), strokeWidth = 8f)
                                        }

                                        // Central logo overlay
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(CircleShape)
                                                .background(PrimaryGreen),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.QrCode,
                                                contentDescription = "qr",
                                                tint = Color.White,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "Scan with your phone camera or payment app to complete.",
                                    fontSize = 11.sp,
                                    color = Color.Gray,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(horizontal = 24.dp)
                                )

                                Spacer(modifier = Modifier.height(24.dp))

                                Button(
                                    onClick = {
                                        onTriggerDispense {
                                            onStepTransition(RefillStep.SUCCESS_DISPENSE)
                                        }
                                    },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                    modifier = Modifier.fillMaxWidth().height(48.dp)
                                ) {
                                    Text(text = "Simulate Successful Pay & Refill", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        RefillStep.SUCCESS_DISPENSE -> {
                            val activeFinalPrice = when (selectedAmountMl) {
                                200 -> 1200
                                500 -> 3000
                                1000 -> 5500
                                else -> selectedAmountMl * 6
                            }
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (isDispensing) {
                                    // 1. ACTIVE LAUNDRY DISPENSING PROGRESS SIMULATOR
                                    Text(
                                        text = "Kiosk Dispensing Detergent...",
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = DarkForest,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = "Please place your bottle under the RefillGo Smart nozzle now.",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    // Dynamic Fluid Pouring Simulation Box
                                    Box(
                                        modifier = Modifier
                                            .size(width = 110.dp, height = 200.dp)
                                            .background(Color(0xFFE5E7EB), RoundedCornerShape(20.dp))
                                            .border(2.dp, Color.Gray, RoundedCornerShape(20.dp))
                                            .clip(RoundedCornerShape(20.dp))
                                    ) {
                                        // Pouring stream nozzle line
                                        Box(
                                            modifier = Modifier
                                                .width(6.dp)
                                                .fillMaxHeight()
                                                .align(Alignment.TopCenter)
                                                .background(PrimaryGreen.copy(alpha = 0.7f))
                                        )

                                        // Fluid Filling Layer - Height is bound to compile-time progress
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .fillMaxHeight(dispenseProgress)
                                                .align(Alignment.BottomCenter)
                                                .background(
                                                    Brush.verticalGradient(
                                                        listOf(PrimaryGreen.copy(alpha = 0.8f), DarkForest)
                                                    )
                                                )
                                        ) {
                                            Text(
                                                text = "${(dispenseProgress * 100).toInt()}%",
                                                color = Color.White,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.align(Alignment.Center)
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Filling: ${(selectedAmountMl * dispenseProgress).toInt()} ml poured",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryGreen
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.size(24.dp))

                                } else {
                                    // 2. DISPENSE FINISHED SCREEN
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Success",
                                        tint = PrimaryGreen,
                                        modifier = Modifier.size(80.dp).padding(vertical = 8.dp)
                                    )

                                    Text(
                                        text = "Dispense Complete! 🌱",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = DarkForest
                                    )
                                    Text(
                                        text = "Thank you for supporting dormitory zero-waste targets.",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(vertical = 4.dp),
                                        textAlign = TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Metric highlights card
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
                                        modifier = Modifier.fillMaxWidth().border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(12.dp))
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                                            horizontalArrangement = Arrangement.SpaceAround
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = "$selectedAmountMl ml", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkForest)
                                                Text(text = "Volume Poured", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = "₩$activeFinalPrice", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = DarkForest)
                                                Text(text = "Paid digitally", fontSize = 10.sp, color = Color.Gray)
                                            }
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(text = "1 Bottle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PrimaryGreen)
                                                Text(text = "Plastic saved", fontSize = 10.sp, color = Color.Gray)
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Button(
                                        onClick = {
                                            // Reset flow State
                                            onStepTransition(RefillStep.SELECT_AMOUNT)
                                            onNavigateToDashboard()
                                        },
                                        shape = RoundedCornerShape(12.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                                        modifier = Modifier.fillMaxWidth().height(48.dp)
                                    ) {
                                        Text(text = "Check Dashboard Statistics", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
