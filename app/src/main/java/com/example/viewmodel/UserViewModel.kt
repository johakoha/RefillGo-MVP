package com.example.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val refillRepository: RefillRepository
) : AndroidViewModel(application) {

    // SharedPreferences for Session persistence
    private val prefs = application.getSharedPreferences("refillgo_sessions", Context.MODE_PRIVATE)

    // Logged-in session flags
    var isLoggedIn by mutableStateOf(false)
        private set

    var loggedInUserId by mutableStateOf<Int?>(null)
        private set

    var currentUser by mutableStateOf<User?>(null)
        private set

    // Real-time user refill history flow
    private val _userRefills = MutableStateFlow<List<RefillLog>>(emptyList())
    val userRefills: StateFlow<List<RefillLog>> = _userRefills.asStateFlow()

    // Auth screen inputs holding
    var authUsername by mutableStateOf("")
    var authPassword by mutableStateOf("")
    var authPhone by mutableStateOf("")
    var authConfirmPassword by mutableStateOf("")

    // Auth Status Message
    var authErrorMsg by mutableStateOf<String?>(null)

    init {
        // Automatically restore user state if remembered previously
        val isSavedLoggedIn = prefs.getBoolean("isLoggedIn", false)
        val savedUserId = prefs.getInt("loggedInUserId", -1)
        if (isSavedLoggedIn && savedUserId != -1) {
            viewModelScope.launch {
                val userObj = userRepository.getUserById(savedUserId)
                if (userObj != null) {
                    currentUser = userObj
                    loggedInUserId = savedUserId
                    isLoggedIn = true
                    loadRefillsForUser(savedUserId)
                } else {
                    // Fallback if user vanished
                    logout()
                }
            }
        }
    }

    /**
     * Authenticate and cache user profile
     */
    fun loginUser(onSuccess: () -> Unit) {
        val username = authUsername.trim()
        val password = authPassword.trim()

        if (username.isEmpty() || password.isEmpty()) {
            authErrorMsg = "Username and Password cannot be empty."
            return
        }

        viewModelScope.launch {
            val user = userRepository.authenticateUser(username, password)
            if (user != null) {
                currentUser = user
                loggedInUserId = user.id
                isLoggedIn = true
                authErrorMsg = null

                // Save session in SharedPreferences for seamless automatic restore
                prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putInt("loggedInUserId", user.id)
                    .apply()

                loadRefillsForUser(user.id)
                onSuccess()
            } else {
                authErrorMsg = "Invalid Username or Password."
            }
        }
    }

    /**
     * Register new Account and automatically login
     */
    fun signupUser(onSuccess: () -> Unit) {
        val username = authUsername.trim()
        val phone = authPhone.trim()
        val password = authPassword.trim()
        val confirmPass = authConfirmPassword.trim()

        if (username.length < 3) {
            authErrorMsg = "Username must be at least 3 characters."
            return
        }
        if (phone.isEmpty()) {
            authErrorMsg = "Phone number is required."
            return
        }
        if (password.length < 5) {
            authErrorMsg = "Password must be at least 5 characters."
            return
        }
        if (password != confirmPass) {
            authErrorMsg = "Passwords do not match."
            return
        }

        viewModelScope.launch {
            // Check if username already exists
            val existing = userRepository.getUserByUsername(username)
            if (existing != null) {
                authErrorMsg = "Username already taken."
                return@launch
            }

            // Create default user profile stats mapping Room variables perfectly
            val newUser = User(
                username = username,
                phoneNumber = phone,
                password = password,
                createdAt = System.currentTimeMillis()
            )

            val createdId = userRepository.registerUser(newUser)
            val createdUser = userRepository.getUserById(createdId)

            if (createdUser != null) {
                currentUser = createdUser
                loggedInUserId = createdId
                isLoggedIn = true
                authErrorMsg = null

                // Save session
                prefs.edit()
                    .putBoolean("isLoggedIn", true)
                    .putInt("loggedInUserId", createdId)
                    .apply()

                // Generate 2 realistic initial refills to make timeline feel active right away!
                seedInitialRefillsForNewUser(createdId)

                loadRefillsForUser(createdId)
                onSuccess()
            } else {
                authErrorMsg = "Failed to create user. Please try again."
            }
        }
    }

    /**
     * End active session
     */
    fun logout() {
        prefs.edit().clear().apply()
        currentUser = null
        loggedInUserId = null
        isLoggedIn = false
        _userRefills.value = emptyList()
        clearInputs()
    }

    fun clearInputs() {
        authUsername = ""
        authPassword = ""
        authPhone = ""
        authConfirmPassword = ""
        authErrorMsg = null
    }

    /**
     * Updates current user from database to keep statistics instantly accurate
     */
    fun refreshCurrentUser() {
        val uId = loggedInUserId ?: return
        viewModelScope.launch {
            val updated = userRepository.getUserById(uId)
            if (updated != null) {
                currentUser = updated
            }
        }
    }

    /**
     * Triggered automatically down reflection when a user completes a refill!
     */
    fun recordUserRefillAction(
        stationName: String,
        amountMl: Int,
        priceKrw: Int
    ) {
        val uId = loggedInUserId ?: return
        viewModelScope.launch {
            // 1. Insert detailed log linked to the User ID in Room Database
            val log = RefillLog(
                stationName = stationName,
                amountMl = amountMl,
                priceKrw = priceKrw,
                timestamp = System.currentTimeMillis(),
                userId = uId,
                paymentMethod = listOf("KakaoPay", "T-Money", "NaverPay", "Credit Card").random(),
                status = "Completed"
            )
            refillRepository.insertLog(log)

            // 2. Increment aggregate stats in users table and sync impact metrics
            userRepository.recordUserRefillStats(uId, amountMl, priceKrw)

            // 3. Refresh user viewmodels
            refreshCurrentUser()
            loadRefillsForUser(uId)
        }
    }

    /**
     * Queries refill logs matching the current user id
     */
    fun loadRefillsForUser(userId: Int) {
        viewModelScope.launch {
            refillRepository.allLogs.collect { list ->
                val userList = list.filter { it.userId == userId }
                _userRefills.value = userList
            }
        }
    }

    /**
     * Quick setup helper to add a couple starting activities to newly registered users
     */
    private suspend fun seedInitialRefillsForNewUser(userId: Int) {
        val r1 = RefillLog(
            stationName = "Wooryong Hall A (Basement Laundry B1)",
            amountMl = 500,
            priceKrw = 3000,
            timestamp = System.currentTimeMillis() - 86400 * 1000 * 2, // 2 days ago
            userId = userId,
            paymentMethod = "KakaoPay",
            status = "Completed"
        )
        val r2 = RefillLog(
            stationName = "Biryong Hall Laundry Hub",
            amountMl = 200,
            priceKrw = 1200,
            timestamp = System.currentTimeMillis() - 3600 * 1000 * 5, // 5 hours ago
            userId = userId,
            paymentMethod = "T-Money",
            status = "Completed"
        )
        refillRepository.insertLogs(listOf(r1, r2))
        
        userRepository.recordUserRefillStats(userId, 700, 4200)
    }

    /**
     * Part 10 — DEMO USER GENERATOR
     * Generates active campus statistics, seed refilled values and live heatmap items.
     */
    fun triggerDemoUserGenerator(onGenerated: (String) -> Unit) {
        viewModelScope.launch {
            val demoStudents = listOf(
                Triple("student_001", "010-4412-0001", "Wooryong Hall A"),
                Triple("student_002", "010-8273-0002", "Wooryong Hall B"),
                Triple("student_003", "010-9092-0003", "Biryong Hall")
            )

            onGenerated("Initiating simulation: Creating demo students in Room...")

            for (student in demoStudents) {
                // Check if already exist
                val existing = userRepository.getUserByUsername(student.first)
                val uId = if (existing == null) {
                    val nUser = User(
                        username = student.first,
                        phoneNumber = student.second,
                        password = "demopassword",
                        createdAt = System.currentTimeMillis() - 86400 * 1000 * 12 // 12 days member
                    )
                    userRepository.registerUser(nUser)
                } else {
                    existing.id
                }

                // Inject 3-5 randomized transaction histories per student
                val volumeSeedList = listOf(200, 500, 1000)
                val randCount = 3 + (0..3).random()
                
                var totalV = 0
                var totalR = 0
                var totalCost = 0
                val studentLogs = mutableListOf<RefillLog>()
                
                for (i in 0 until randCount) {
                    val vol = volumeSeedList.random()
                    val cost = when (vol) {
                        200 -> 1200
                        500 -> 3000
                        1000 -> 5500
                        else -> vol * 6
                    }
                    totalV += vol
                    totalR++
                    totalCost += cost

                    val randTime = System.currentTimeMillis() - (1..10).random() * 86400000L - (1..20).random() * 3600000L
                    val log = RefillLog(
                        stationName = student.third,
                        amountMl = vol,
                        priceKrw = cost,
                        timestamp = randTime,
                        userId = uId,
                        paymentMethod = listOf("KakaoPay", "T-Money", "NaverPay").random(),
                        status = "Completed"
                    )
                    studentLogs.add(log)
                }
                refillRepository.insertLogs(studentLogs)

                val bottles = (totalV / 500)
                val plastic = (totalV / 500.0) * 0.025
                val co2 = (totalV / 500.0) * 0.190

                userRepository.updateUserStatsDirect(
                    userId = uId,
                    refills = totalR,
                    volume = totalV,
                    bottles = bottles,
                    plastic = plastic,
                    co2 = co2,
                    moneySaved = totalCost.toDouble()
                )
            }

            onGenerated("Active students registered! Statistics, heatmaps, and transaction histories populated successfully.")
        }
    }
}

class UserViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val refillRepository: RefillRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(application, userRepository, refillRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
