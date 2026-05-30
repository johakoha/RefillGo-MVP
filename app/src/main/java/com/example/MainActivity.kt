package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Diversity3
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalLaundryService
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Room
import com.example.data.AppDatabase
import com.example.data.RefillRepository
import com.example.data.UserRepository
import com.example.viewmodel.RefillScreen
import com.example.viewmodel.RefillStep
import com.example.viewmodel.RefillViewModel
import com.example.viewmodel.RefillViewModelFactory
import com.example.viewmodel.UserViewModel
import com.example.viewmodel.UserViewModelFactory
import com.example.ui.components.ScannerMockup
import com.example.ui.screens.CampusMapPage
import com.example.ui.screens.ExpansionPage
import com.example.ui.screens.LandingPage
import com.example.ui.screens.RefillFlowPage
import com.example.ui.screens.SustainabilityPage
import com.example.ui.screens.TeamPage
import com.example.ui.screens.InvestorPage
import com.example.ui.screens.AuthPage
import com.example.ui.screens.ProfilePage
import androidx.compose.material.icons.filled.Person
import com.example.ui.theme.DarkForest
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.SecondaryBlue

class MainActivity : ComponentActivity() {

    // Lazy initialization of Database and active Repository for injection
    private val database by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "refillgo_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    private val repository by lazy {
        RefillRepository(database.refillDao())
    }

    private val userRepository by lazy {
        UserRepository(database.userDao(), database.impactMetricsDao())
    }

    // Bind ViewModel utilizing Custom Injection Factory
    private val refillViewModel: RefillViewModel by viewModels {
        RefillViewModelFactory(application, repository)
    }

    private val userViewModel: UserViewModel by viewModels {
        UserViewModelFactory(application, userRepository, repository)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                if (!userViewModel.isLoggedIn) {
                    AuthPage(
                        userViewModel = userViewModel,
                        onAuthSuccess = {
                            refillViewModel.currentScreen = RefillScreen.LANDING
                        }
                    )
                } else {
                    val currentScreen = refillViewModel.currentScreen
                    val logs by refillViewModel.refillLogs.collectAsState()
                    val liveFeed by refillViewModel.liveActivityFeed.collectAsState()

                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(
                            title = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .background(PrimaryGreen, RoundedCornerShape(8.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .border(2.dp, Color.White, CircleShape)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "RefillGo",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 20.sp,
                                        letterSpacing = (-0.5).sp
                                    )
                                }
                            },
                            actions = {
                                // Right Top action button to quickly launch holographic scan Overlay dialog
                                IconButton(onClick = { refillViewModel.isQrScannerOpen = true }) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = "Open camera scanner",
                                        tint = PrimaryGreen
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = Color.White,
                                titleContentColor = DarkForest
                            )
                        )
                    },
                    bottomBar = {
                        NavigationBar(
                            containerColor = Color.White,
                            tonalElevation = 8.dp
                        ) {
                            // 1. Landing Home Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.LANDING,
                                onClick = { refillViewModel.currentScreen = RefillScreen.LANDING },
                                icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                                label = { Text("Home", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 2. Campus Map Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.MAP,
                                onClick = { refillViewModel.currentScreen = RefillScreen.MAP },
                                icon = { Icon(Icons.Default.Map, contentDescription = "Map") },
                                label = { Text("Map", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 3. Refill Flow Active Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.REFILL_FLOW,
                                onClick = { refillViewModel.currentScreen = RefillScreen.REFILL_FLOW },
                                icon = {
                                    BadgedBox(badge = {
                                        if (refillViewModel.scannedSuccessfully && refillViewModel.currentStep == RefillStep.SELECT_AMOUNT) {
                                            Badge(containerColor = PrimaryGreen) { Text("Scan") }
                                        }
                                    }) {
                                        Icon(Icons.Default.LocalLaundryService, contentDescription = "Refill")
                                    }
                                },
                                label = { Text("Refill", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 4. Sustainability Dashboard Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.DASHBOARD,
                                onClick = { refillViewModel.currentScreen = RefillScreen.DASHBOARD },
                                icon = { Icon(Icons.Default.QueryStats, contentDescription = "Ecometrics") },
                                label = { Text("Stats", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 5. Team Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.TEAM,
                                onClick = { refillViewModel.currentScreen = RefillScreen.TEAM },
                                icon = { Icon(Icons.Default.Diversity3, contentDescription = "Team Pioneers") },
                                label = { Text("Team", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 6. Roadmap Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.EXPLORE_ROADMAP,
                                onClick = { refillViewModel.currentScreen = RefillScreen.EXPLORE_ROADMAP },
                                icon = { Icon(Icons.Default.Timeline, contentDescription = "Roadmap") },
                                label = { Text("Roadmap", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 7. Startup Pitch / Investor Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.INVESTOR,
                                onClick = { refillViewModel.currentScreen = RefillScreen.INVESTOR },
                                icon = { Icon(Icons.Default.Star, contentDescription = "Pitch") },
                                label = { Text("Pitch", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )

                            // 8. Profile Tab
                            NavigationBarItem(
                                selected = currentScreen == RefillScreen.PROFILE,
                                onClick = { refillViewModel.currentScreen = RefillScreen.PROFILE },
                                icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                                label = { Text("Profile", fontSize = 10.sp) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryGreen,
                                    selectedTextColor = PrimaryGreen,
                                    indicatorColor = PrimaryGreen.copy(alpha = 0.1f)
                                )
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Switch over active selected sheet to render
                        when (currentScreen) {
                            RefillScreen.LANDING -> {
                                val lastLog = logs.maxByOrNull { it.timestamp }
                                val lastRefillTimeText = if (lastLog != null) {
                                    val diffMs = System.currentTimeMillis() - lastLog.timestamp
                                    val diffSec = maxOf(0L, diffMs / 1000L)
                                    if (diffSec < 5) "Just now"
                                    else if (diffSec < 60) "${diffSec}s ago"
                                    else if (diffSec < 3600) "${diffSec / 60}m ago"
                                    else "${diffSec / 3600}h ago"
                                } else {
                                    "7m ago"
                                }

                                LandingPage(
                                    totalRefills = logs.size,
                                    liveFeed = liveFeed,
                                    isDemoModeEnabled = refillViewModel.isDemoModeEnabled,
                                    onDemoModeToggle = { refillViewModel.isDemoModeEnabled = it },
                                    lastRefillTimeText = lastRefillTimeText,
                                    onNavigateToMap = { refillViewModel.currentScreen = RefillScreen.MAP },
                                    onStartScan = { refillViewModel.isQrScannerOpen = true }
                                )
                            }
                            RefillScreen.MAP -> {
                                CampusMapPage(
                                    stations = refillViewModel.campusStations,
                                    selectedStation = refillViewModel.selectedStation,
                                    onStationSelected = { refillViewModel.selectedStation = it },
                                    onNavigateToRefill = {
                                        refillViewModel.currentScreen = RefillScreen.REFILL_FLOW
                                        refillViewModel.currentStep = RefillStep.SELECT_AMOUNT
                                    }
                                )
                            }
                            RefillScreen.REFILL_FLOW -> {
                                RefillFlowPage(
                                    station = refillViewModel.selectedStation,
                                    activeStep = refillViewModel.currentStep,
                                    selectedAmountMl = refillViewModel.selectedAmountMl,
                                    isDispensing = refillViewModel.isDispensing,
                                    dispenseProgress = refillViewModel.dispenseProgress,
                                    onAmountSelected = { refillViewModel.selectedAmountMl = it },
                                    onStepTransition = { refillViewModel.currentStep = it },
                                    onTriggerDispense = { onDone ->
                                        refillViewModel.triggerDispense {
                                            onDone()
                                            val activeHubName = refillViewModel.selectedStation?.name ?: "Wooryong Hall B"
                                            val priceAmount = refillViewModel.calculatePrice(refillViewModel.selectedAmountMl)
                                            userViewModel.recordUserRefillAction(
                                                stationName = activeHubName,
                                                amountMl = refillViewModel.selectedAmountMl,
                                                priceKrw = priceAmount
                                             )
                                        }
                                    },
                                    onNavigateToDashboard = { refillViewModel.currentScreen = RefillScreen.DASHBOARD }
                                )
                            }
                            RefillScreen.DASHBOARD -> {
                                SustainabilityPage(
                                    viewModel = refillViewModel,
                                    logs = logs
                                )
                            }
                            RefillScreen.TEAM -> {
                                TeamPage()
                            }
                            RefillScreen.EXPLORE_ROADMAP -> {
                                ExpansionPage()
                            }
                            RefillScreen.INVESTOR -> {
                                InvestorPage(
                                    isDemoRunning = refillViewModel.isInvestorDemoRunning,
                                    demoStatusText = refillViewModel.investorDemoStatus,
                                    onStartDemo = { refillViewModel.startInvestorDemo() }
                                )
                            }
                            RefillScreen.PROFILE -> {
                                ProfilePage(userViewModel = userViewModel)
                            }
                        }

                        // Superimposed camera hologram QR Scanner overlays
                        if (refillViewModel.isQrScannerOpen) {
                            ScannerMockup(
                                onScanSuccess = { stationId ->
                                    refillViewModel.handleQrScan(stationId)
                                },
                                onCancel = {
                                    refillViewModel.isQrScannerOpen = false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}
}

