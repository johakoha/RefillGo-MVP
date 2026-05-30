package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue
import com.example.ui.theme.WaterCyan

@Composable
fun DetergentRefillBarChart(
    data: List<Float>, // float data values normalized
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Baseline axis separator
            drawLine(
                Color(0xFFE5E7EB),
                Offset(0f, h - 30f),
                Offset(w, h - 30f),
                strokeWidth = 2f
            )

            if (data.isEmpty()) return@Canvas

            // Calculate spacing
            val barCount = data.size
            val barWidth = (w / barCount) * 0.45f
            val spacing = (w / barCount) * 0.55f
            
            val maxValRaw = data.maxOrNull() ?: 1f
            val maxVal = if (maxValRaw == 0f) 1f else maxValRaw
            val chartHeight = h - 60f

            for (i in data.indices) {
                val value = data[i]
                val normalizedHeight = (value / maxVal) * chartHeight
                
                val xOffset = i * (barWidth + spacing) + (spacing / 2f)
                val yOffset = h - 30f - normalizedHeight

                // Draw gradient fill bar
                drawRoundRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(PrimaryGreen, SecondaryBlue)
                    ),
                    topLeft = Offset(xOffset, yOffset),
                    size = Size(barWidth, normalizedHeight),
                    cornerRadius = CornerRadius(12f, 12f)
                )

                // Optional grid values or lines
                drawLine(
                    Color(0xFFF3F4F6),
                    Offset(xOffset + barWidth / 2f, 0f),
                    Offset(xOffset + barWidth / 2f, yOffset),
                    strokeWidth = 1f
                )
            }
        }
    }
}

@Composable
fun WaterFootprintLineChart(
    dataPoints: List<Float>, // water saved metrics in Liters
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .background(Color.White)
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            val gridLines = 4
            val gridColor = Color(0xFFF3F4F6)
            for (g in 0..gridLines) {
                val yGrid = (h - 30f) * g / gridLines
                drawLine(gridColor, Offset(0f, yGrid), Offset(w, yGrid), strokeWidth = 1f)
            }

            if (dataPoints.size < 2) return@Canvas

            val maxValRaw = dataPoints.maxOrNull() ?: 100f
            val maxVal = if (maxValRaw == 0f) 1f else maxValRaw
            val pointsCount = dataPoints.size
            val xStep = w / (pointsCount - 1)
            val chartHeight = h - 40f

            val path = Path()
            val fillPath = Path()

            // Starting point
            val startX = 0f
            val startY = h - 30f - (dataPoints[0] / maxVal) * chartHeight
            path.moveTo(startX, startY)
            fillPath.moveTo(startX, h - 30f)
            fillPath.lineTo(startX, startY)

            for (i in 1 until pointsCount) {
                val x = i * xStep
                val y = h - 30f - (dataPoints[i] / maxVal) * chartHeight
                
                // Draw smooth curve using Beziers
                val prevX = (i - 1) * xStep
                val prevY = h - 30f - (dataPoints[i - 1] / maxVal) * chartHeight
                val controlX1 = prevX + (x - prevX) / 2f
                val controlY1 = prevY
                val controlX2 = prevX + (x - prevX) / 2f
                val controlY2 = y

                path.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                fillPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                
                if (i == pointsCount - 1) {
                    fillPath.lineTo(x, h - 30f)
                }
            }

            // Fill gradient under curve
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        WaterCyan.copy(alpha = 0.35f),
                        WaterCyan.copy(alpha = 0.05f),
                        Color.Transparent
                    )
                )
            )

            // Draw primary curve line
            drawPath(
                path = path,
                color = WaterCyan,
                style = Stroke(width = 6f)
            )

            // Draw data indicator points
            for (i in dataPoints.indices) {
                val x = i * xStep
                val y = h - 30f - (dataPoints[i] / maxVal) * chartHeight
                drawCircle(
                    color = Color.White,
                    radius = 8f,
                    center = Offset(x, y)
                )
                drawCircle(
                    color = WaterCyan,
                    radius = 5f,
                    center = Offset(x, y)
                )
            }
        }
    }
}
