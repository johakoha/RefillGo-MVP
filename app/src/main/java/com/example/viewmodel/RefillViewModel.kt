package com.example.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.AppDatabase
import com.example.data.GeminiConfig
import com.example.data.GeminiContent
import com.example.data.GeminiPart
import com.example.data.GeminiRequest
import com.example.data.GeminiRetrofitClient
import com.example.data.RefillLog
import com.example.data.RefillRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

enum class RefillScreen {
    LANDING, MAP, REFILL_FLOW, DASHBOARD, TEAM, EXPLORE_ROADMAP, INVESTOR, PROFILE
}

enum class RefillStep {
    SELECT_AMOUNT, PRICE_CALC, QR_PAY, SUCCESS_DISPENSE
}

// Data class representing Inha University station
data class CampusStation(
    val id: String,
    val name: String,
    val locationDescription: String,
    val washerCount: Int,
    val dryerCount: Int,
    val latitude: Double,
    val longitude: Double,
    val currentActivityLevel: String, // "High", "Medium", "Idle"
    val plasticSavedCount: Int,
    val status: String,
    val refillsToday: Int,
    val availableDetergent: String
)

class RefillViewModel(
    application: Application,
    private val repository: RefillRepository
) : AndroidViewModel(application) {

    // Main Navigation
    var currentScreen by mutableStateOf(RefillScreen.LANDING)

    // Professor Demo Mode state
    var isDemoModeEnabled by mutableStateOf(false)

    // Startup pitch state variables
    var isInvestorDemoRunning by mutableStateOf(false)
    var investorDemoStatus by mutableStateOf("")

    fun startInvestorDemo() {
        if (isInvestorDemoRunning) return
        viewModelScope.launch {
            isInvestorDemoRunning = true
            isDemoModeEnabled = true
            
            // Step 1: Welcome & Landing Page
            currentScreen = RefillScreen.LANDING
            investorDemoStatus = "Welcome to RefillGo! Displaying real-time campus environmental impact stats..."
            delay(4000)
            
            // Step 2: Interactive Mapbox Campus Hubs
            currentScreen = RefillScreen.MAP
            investorDemoStatus = "Opening active smart nodes map. Panning to Inha University..."
            delay(1500)
            // Trigger auto selecting a station
            val randomStation = campusStations.firstOrNull() ?: campusStations[0]
            selectedStation = randomStation
            investorDemoStatus = "Selecting active hub: ${randomStation.name} in 3D pitch view."
            delay(3500)
            
            // Step 3: Trigger active Refill Flow simulation
            currentScreen = RefillScreen.REFILL_FLOW
            currentStep = RefillStep.SELECT_AMOUNT
            investorDemoStatus = "Initializing Refill. Step 1: Selecting dispenser amount..."
            delay(2000)
            selectedAmountMl = 500
            currentStep = RefillStep.PRICE_CALC
            investorDemoStatus = "Step 2: Performing localized unit price calculation..."
            delay(1500)
            currentStep = RefillStep.QR_PAY
            investorDemoStatus = "Step 3: Awaiting secure student QR code scan..."
            delay(2000)
            
            // Simulate dispense action
            triggerDispense {
                currentStep = RefillStep.SUCCESS_DISPENSE
                investorDemoStatus = "Disposal averted! Dispense completed. Carbon footprint reduced."
            }
            delay(4000)
            
            // Step 4: Analytics
            currentScreen = RefillScreen.DASHBOARD
            investorDemoStatus = "Reviewing sustainability KPIs, Room metrics, and ML predictions..."
            delay(4000)
            
            // Step 5: Finish at Investor Dashboard
            currentScreen = RefillScreen.INVESTOR
            investorDemoStatus = "Demonstrating commercial viability, IoT kiosk design, and prototype gallery."
            delay(4000)
            isInvestorDemoRunning = false
            investorDemoStatus = ""
        }
    }

    // Database Logs
    val refillLogs: StateFlow<List<RefillLog>> = repository.allLogs
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current Station Selected
    var selectedStation by mutableStateOf<CampusStation?>(null)

    // Live Refill Flow States
    var currentStep by mutableStateOf(RefillStep.SELECT_AMOUNT)
    var selectedAmountMl by mutableStateOf(500) // 200, 500, 1000
    var dispenseProgress by mutableStateOf(0f)
    var isDispensing by mutableStateOf(false)
    var isQrScannerOpen by mutableStateOf(false)
    var scannedSuccessfully by mutableStateOf(false)

    // Campus Stations (Inha University Dorms)
    var campusStations by mutableStateOf(listOf(
        CampusStation(
            id = "inha-wooryong-a",
            name = "Wooryong Hall A",
            locationDescription = "Laundry Area, Basement Floor 1",
            washerCount = 8,
            dryerCount = 8,
            latitude = 37.4503,
            longitude = 126.6543,
            currentActivityLevel = "High",
            plasticSavedCount = 3410,
            status = "Active",
            refillsToday = 37,
            availableDetergent = "14.2L"
        ),
        CampusStation(
            id = "inha-wooryong-b",
            name = "Wooryong Hall B",
            locationDescription = "Main Hallway Laundry Room, 1F",
            washerCount = 10,
            dryerCount = 10,
            latitude = 37.4511,
            longitude = 126.6535,
            currentActivityLevel = "Medium",
            plasticSavedCount = 5230,
            status = "Active",
            refillsToday = 28,
            availableDetergent = "9.7L"
        ),
        CampusStation(
            id = "inha-biryong",
            name = "Biryong Hall",
            locationDescription = "Rec Building Laundry Hub, B1",
            washerCount = 16,
            dryerCount = 12,
            latitude = 37.4495,
            longitude = 126.6528,
            currentActivityLevel = "High",
            plasticSavedCount = 4891,
            status = "Active",
            refillsToday = 41,
            availableDetergent = "16.5L"
        ),
        CampusStation(
            id = "inha-global-house",
            name = "Global House",
            locationDescription = "International Wing Lobby, 1F",
            washerCount = 12,
            dryerCount = 10,
            latitude = 37.4520,
            longitude = 126.6549,
            currentActivityLevel = "Idle",
            plasticSavedCount = 904,
            status = "Pilot Location",
            refillsToday = 18,
            availableDetergent = "7.4L"
        )
    ))

    init {
        // Seed database logs dynamically if empty to keep charts beautiful
        viewModelScope.launch {
            repository.seedMockDataIfEmpty()
            // Set initial selected station
            selectedStation = campusStations[1]
        }
        
        // Generate live ambient campus events
        startLiveActivityFeed()
        // Initialize Demo Mode loop
        startDemoModeLoop()
    }

    // Dynamic Live Activity Feed items
    private val _liveActivityFeed = MutableStateFlow<List<String>>(
        listOf(
            "Wooryong Hall B: Refilled 500ml of Eco-Sud 2 mins ago",
            "Biryong Hall: saved 2 plastic bottles 12 mins ago",
            "Global House: Dispensed 200ml detergent 25 mins ago",
            "Wooryong Hall A: Carbon footprint reduced by 110g CO2"
        )
    )
    val liveActivityFeed: StateFlow<List<String>> = _liveActivityFeed.asStateFlow()

    private fun startLiveActivityFeed() {
        viewModelScope.launch {
            val randomStations = listOf("Wooryong Hall A", "Wooryong Hall B", "Biryong Hall", "Global House")
            val randomProducts = listOf("Eco-Sud Organic Green", "Pure-Dish Lemon Clean", "Sensiclean Lavender")
            val randomAmounts = listOf(200, 500, 1000)
            
            while (true) {
                delay(12000) // Append custom event every 12 seconds
                if (!isDemoModeEnabled) {
                    val station = randomStations.random()
                    val product = randomProducts.random()
                    val amt = randomAmounts.random()
                    val list = _liveActivityFeed.value.toMutableList()
                    list.add(0, "$station: Just refilled $amt ml of $product!")
                    if (list.size > 8) {
                        list.removeAt(list.size - 1)
                    }
                    _liveActivityFeed.value = list
                } else {
                    delay(4000)
                }
            }
        }
    }

    private fun startDemoModeLoop() {
        viewModelScope.launch {
            val randomProducts = listOf("Eco-Sud Organic Green", "Pure-Dish Lemon Clean", "Sensiclean Lavender")
            val randomAmounts = listOf(200, 500, 1000)
            
            while (true) {
                delay(4000) // Trigger realistic transaction every 4 seconds
                if (isDemoModeEnabled) {
                    val randomStation = campusStations.random()
                    val amtMl = randomAmounts.random()
                    val product = randomProducts.random()
                    
                    // Insert real transaction into Room DB
                    val price = calculatePrice(amtMl)
                    val log = RefillLog(
                        stationName = randomStation.name,
                        amountMl = amtMl,
                        priceKrw = price,
                        timestamp = System.currentTimeMillis()
                    )
                    repository.insertLog(log)
                    
                    // Update campusStations stats dynamically
                    campusStations = campusStations.map { st ->
                        if (st.id == randomStation.id) {
                            val currentVolText = st.availableDetergent.replace("L", "").toDoubleOrNull() ?: 10.0
                            val decVol = amtMl / 1000.0
                            val newVol = maxOf(0.1, currentVolText - decVol)
                            val updatedRefillsToday = st.refillsToday + 1
                            
                            st.copy(
                                refillsToday = updatedRefillsToday,
                                availableDetergent = String.format(Locale.US, "%.1fL", newVol),
                                plasticSavedCount = st.plasticSavedCount + 1
                            )
                        } else {
                            st
                        }
                    }
                    
                    // Sync active bottom sheet state
                    selectedStation?.let { currentSel ->
                        if (currentSel.id == randomStation.id) {
                            selectedStation = campusStations.find { it.id == randomStation.id }
                        }
                    }
                    
                    // Prepend to activity feed
                    val list = _liveActivityFeed.value.toMutableList()
                    list.add(0, "${randomStation.name}: Saved 1 bottle! Refilled $amtMl ml of $product.")
                    if (list.size > 8) {
                        list.removeAt(list.size - 1)
                    }
                    _liveActivityFeed.value = list
                }
            }
        }
    }

    // Interactive Impact Calculator State
    var laundryLoadsPerMonth by mutableStateOf(6f) // Slider: 1 to 20
    var detergentRetailPricePerMl by mutableStateOf(10f) // Slider: ₩5 - ₩20 per ml

    // Dynamic calculator output variables
    val calVolumeMlPerLoad = 120 // standard single load mL
    
    // AI Predictions
    var aiPredictionState by mutableStateOf<PredictionState>(PredictionState.Idle)
        private set

    sealed class PredictionState {
        object Idle : PredictionState()
        object Loading : PredictionState()
        data class Success(val predictionText: String, val isLive: Boolean) : PredictionState()
        data class Error(val errorMessage: String) : PredictionState()
    }

    // Rate Calculation: 200ml = 1200 Won, 500ml = 3000 Won, 1L = 5500 Won
    fun calculatePrice(amountMl: Int): Int {
        return when (amountMl) {
            200 -> 1200
            500 -> 3000
            1000 -> 5500
            else -> amountMl * 6
        }
    }

    // QR scanner scanned a detergent station
    fun handleQrScan(stationCode: String) {
        val matched = campusStations.find { it.id == stationCode }
        if (matched != null) {
            selectedStation = matched
            scannedSuccessfully = true
            isQrScannerOpen = false
            currentScreen = RefillScreen.REFILL_FLOW
            currentStep = RefillStep.SELECT_AMOUNT
        }
    }

    // Execute Dispense Log and Simulation
    fun triggerDispense(onDone: () -> Unit) {
        if (isDispensing) return
        isDispensing = true
        dispenseProgress = 0f
        
        viewModelScope.launch {
            // Emulate detergent progress bar filling up dynamically
            for (i in 0..100) {
                delay(35)
                dispenseProgress = i / 100f
            }
            
            // Insert real transaction into Room database
            val station = selectedStation?.name ?: "Wooryong Hall B"
            val price = calculatePrice(selectedAmountMl)
            val log = RefillLog(
                stationName = station,
                amountMl = selectedAmountMl,
                priceKrw = price,
                timestamp = System.currentTimeMillis()
            )
            repository.insertLog(log)
            
            isDispensing = false
            onDone()
        }
    }

    // Flush all past refills
    fun resetDatabase() {
        viewModelScope.launch {
            repository.clearLogs()
            repository.seedMockDataIfEmpty()
        }
    }

    // Live AI predictions using Gemini API
    fun runAiDemandPrediction() {
        aiPredictionState = PredictionState.Loading
        
        viewModelScope.launch {
            val totalRefills = refillLogs.value.size
            val totalVolumeMl = refillLogs.value.sumOf { it.amountMl }
            val totalPlasticSaved = totalVolumeMl / 500.0
            
            // Collect station activity details to feed to AI
            val activitySummary = campusStations.joinToString(", ") { "${it.name}: activity level ${it.currentActivityLevel}" }

            val apiKey = BuildConfig.GEMINI_API_KEY
            val isApiKeyValid = apiKey.isNotEmpty() && apiKey != "MY_GEMINI_API_KEY" && apiKey != "GEMINI_API_KEY"

            if (isApiKeyValid) {
                try {
                    val prompt = """
                        You are the RefillGo Smart Sustainability Forecasting and Campus expansion AI.
                        We install detergent refill stations in university dormitories (Inha University).
                        Here is our real usage data:
                        - Total active stations: 4 (${activitySummary})
                        - Local refills recorded: $totalRefills
                        - Total volume dispensed: $totalVolumeMl ml
                        - Total plastic bottles averted: $totalPlasticSaved (equivalent to 500ml per bottle)
                        
                        Based on this laundry activity, do two things:
                        1. Generate a future detergent demand forecast for next semester at Inha University (predict peak hours, refills, and estimated volume).
                        2. Write a highly concrete, premium 3-paragraph startup strategy on launching Phase 2 (Yonsei University) and Phase 3 (Korea University), explaining how much plastic waste we will mitigate.
                        
                        Give professional metrics, an encouraging eco-conscious vibe, and speak like a top-tier Y-Combinator clean-tech startup. Limit your response to 230 words.
                    """.trimIndent()

                    val request = GeminiRequest(
                        contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = prompt)))),
                        generationConfig = GeminiConfig(temperature = 0.7f)
                    )

                    val response = withContext(Dispatchers.IO) {
                        GeminiRetrofitClient.api.generateContent(apiKey, request)
                    }

                    val responseText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    if (responseText != null) {
                        aiPredictionState = PredictionState.Success(responseText, isLive = true)
                    } else {
                        throw Exception("Empty response from AI engine.")
                    }
                } catch (e: Exception) {
                    // Fallback to rules-based local predictive engine if API limits/networks error
                    generateLocalRuleBasedPrediction(totalRefills, totalVolumeMl, totalPlasticSaved)
                }
            } else {
                // Return immediate gorgeous rules-based prediction
                delay(1200) // Simulating network lag
                generateLocalRuleBasedPrediction(totalRefills, totalVolumeMl, totalPlasticSaved)
            }
        }
    }

    private fun generateLocalRuleBasedPrediction(totalRefills: Int, totalVolumeMl: Int, plasticSaved: Double) {
        val growthPercent = 24 + Random.nextInt(10)
        val forecastLitres = ((totalVolumeMl * 1.25) / 1000).toInt()
        val yonseiMitigationBottles = (plasticSaved * 3.4).toInt()
        val koreaMitigationBottles = (plasticSaved * 4.1).toInt()
        
        val explanation = """
            🌱 [RefillGo Neural Engine: Rule-Based Predictive Forecast]
            
            1. INHA UNIVERSITY DEMAND FORECAST:
            Based on laundry room activities across 4 dormitory hubs, we forecast weekly detergent demand to surge by $growthPercent% next semester. Peak queues are predicted on Sunday evenings (18:00 - 22:00) in Wooryong Hall A, driven by dormitory check-ins. To meet this demand, we recommend refueling the intelligent dispenser tanks to 100% capacity on Friday afternoons and adding a dual-nozzle system in Biryong Dormitory B1 to prevent line congestion.
            
            2. ROADMAP EXTENSION METRICS:
            • Phase 2 (Yonsei University Surchages): Launching 8 new kiosks inside Yonsei residential suites is estimated to mitigate $yonseiMitigationBottles plastic bottles in the first 90 days.
            • Phase 3 (Korea University Expansion): Deploying 12 smart dispensers inside Korea campus houses is forecasted to prevent an estimated $koreaMitigationBottles plastic bottles, translating into 3.2 metric tons of net carbon footprint reduction.
            
            ℹ️ Running in Offline Analytics mode. Add a valid GEMINI_API_KEY to your AI Studio Secrets panel to enable real-time generative forecasting and customized regional clean-tech advice.
        """.trimIndent()
        
        aiPredictionState = PredictionState.Success(explanation, isLive = false)
    }
}

class RefillViewModelFactory(
    private val application: Application,
    private val repository: RefillRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RefillViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RefillViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
