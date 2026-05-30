package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Repository separating User accounts, Credentials, and Eco KPIs.
 * Evolved to support local-first hybrid architecture (Room today, Supabase tomorrow).
 * 
 * FUTURE MIGRATION COMMENTARY:
 * To migrate to Supabase:
 * 1. Implement this interface or class wrapping the Supabase REST Client / GoTrue authentication interface.
 * 2. Authenticate using supabase.auth.signUpWith(email, password) and signInWith(email, password)
 * 3. Save matching profiles to the PostgreSQL "users" table.
 * 4. The ViewModels and Compose views remain untouched because storage mechanisms are fully abstract.
 */
class UserRepository(
    private val userDao: UserDao,
    private val impactMetricsDao: ImpactMetricsDao
) {

    suspend fun getUserById(id: Int): User? {
        return userDao.getUserById(id)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    /**
     * Create a user and initialize default sustainability metrics.
     */
    suspend fun registerUser(user: User): Int {
        val createdUserId = userDao.insertUser(user).toInt()
        
        // Populate default ImpactMetrics profile
        val defaultMetrics = ImpactMetrics(
            userId = createdUserId,
            bottlesSaved = 0,
            plasticSaved = 0.0,
            co2Saved = 0.0,
            moneySaved = 0.0
        )
        impactMetricsDao.insertMetrics(defaultMetrics)
        
        return createdUserId
    }

    /**
     * Check credentials and return logged-in User profile if validated.
     */
    suspend fun authenticateUser(username: String, password: String): User? {
        val user = userDao.getUserByUsername(username)
        if (user != null && user.password == password) {
            return user
        }
        return null
    }

    /**
     * Increment user's aggregate environmental impact metrics instantly.
     */
    suspend fun recordUserRefillStats(
        userId: Int,
        volumeMl: Int,
        priceKrw: Int
    ) {
        val bottles = if (volumeMl == 1000) 2 else 1
        val plasticKg = (volumeMl / 500.0) * 0.025 // 25g plastic saved per 500ml standard container
        val co2PreventedKg = (volumeMl / 500.0) * 0.19 // 190g CO2 averted per container recycled/averted
        
        userDao.incrementUserStats(
            id = userId,
            volume = volumeMl,
            bottles = bottles,
            plastic = plasticKg,
            co2 = co2PreventedKg
        )

        // Sync corresponding Row in impact_metrics table as equivalent to future PostgreSQL table design
        val currentMetrics = impactMetricsDao.getMetricsForUser(userId)
        val updatedMetrics = if (currentMetrics != null) {
            currentMetrics.copy(
                bottlesSaved = currentMetrics.bottlesSaved + bottles,
                plasticSaved = currentMetrics.plasticSaved + plasticKg,
                co2Saved = currentMetrics.co2Saved + co2PreventedKg,
                moneySaved = currentMetrics.moneySaved + priceKrw
            )
        } else {
            ImpactMetrics(
                userId = userId,
                bottlesSaved = bottles,
                plasticSaved = plasticKg,
                co2Saved = co2PreventedKg,
                moneySaved = priceKrw.toDouble()
            )
        }
        impactMetricsDao.insertMetrics(updatedMetrics)
    }

    /**
     * Set explicit stats for a user (e.g. for generating/restoring demo states)
     */
    suspend fun updateUserStatsDirect(
        userId: Int,
        refills: Int,
        volume: Int,
        bottles: Int,
        plastic: Double,
        co2: Double,
        moneySaved: Double
    ) {
        userDao.updateUserStatsDirect(userId, refills, volume, bottles, plastic, co2)
        
        val metrics = ImpactMetrics(
            userId = userId,
            bottlesSaved = bottles,
            plasticSaved = plastic,
            co2Saved = co2,
            moneySaved = moneySaved
        )
        impactMetricsDao.insertMetrics(metrics)
    }
}
