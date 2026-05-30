package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.util.Calendar

class RefillRepository(private val refillDao: RefillDao) {

    val allLogs: Flow<List<RefillLog>> = refillDao.getAllLogs()

    suspend fun insertLog(log: RefillLog) {
        refillDao.insertLog(log)
    }

    suspend fun insertLogs(logs: List<RefillLog>) {
        refillDao.insertLogs(logs)
    }

    suspend fun clearLogs() {
        refillDao.clearAllLogs()
    }

    suspend fun seedMockDataIfEmpty() {
        val count = refillDao.getCount()
        if (count == 0) {
            val now = Calendar.getInstance()
            
            // Seed 14 realistic back-dated refills over the past 2 weeks
            val stations = listOf(
                "Wooryong Hall A (Laundry Room B1)",
                "Wooryong Hall B (Laundry Room 1F)",
                "Biryong Dormitory (Laundry B1)",
                "Student Dorm 1 (Female Floor 1F)",
                "Student Dorm 2 (Men's Hall 1F)"
            )
            
            val initialLogs = listOf(
                RefillLog(stationName = stations[0], amountMl = 500, priceKrw = 3000, timestamp = getDaysAgo(now, 14)),
                RefillLog(stationName = stations[1], amountMl = 1000, priceKrw = 5500, timestamp = getDaysAgo(now, 13)),
                RefillLog(stationName = stations[2], amountMl = 200, priceKrw = 1400, timestamp = getDaysAgo(now, 12)),
                RefillLog(stationName = stations[3], amountMl = 500, priceKrw = 3000, timestamp = getDaysAgo(now, 10)),
                RefillLog(stationName = stations[4], amountMl = 1000, priceKrw = 5500, timestamp = getDaysAgo(now, 9)),
                RefillLog(stationName = stations[0], amountMl = 200, priceKrw = 1400, timestamp = getDaysAgo(now, 7)),
                RefillLog(stationName = stations[1], amountMl = 500, priceKrw = 3000, timestamp = getDaysAgo(now, 6)),
                RefillLog(stationName = stations[2], amountMl = 500, priceKrw = 3000, timestamp = getDaysAgo(now, 5)),
                RefillLog(stationName = stations[3], amountMl = 1000, priceKrw = 5500, timestamp = getDaysAgo(now, 4)),
                RefillLog(stationName = stations[4], amountMl = 200, priceKrw = 1400, timestamp = getDaysAgo(now, 3)),
                RefillLog(stationName = stations[0], amountMl = 500, priceKrw = 3000, timestamp = getDaysAgo(now, 2)),
                RefillLog(stationName = stations[1], amountMl = 1000, priceKrw = 5500, timestamp = getDaysAgo(now, 1)),
                RefillLog(stationName = stations[2], amountMl = 500, priceKrw = 3000, timestamp = getDaysAgo(now, 0) - 4 * 3600 * 1000), // 4 hours ago
                RefillLog(stationName = stations[0], amountMl = 200, priceKrw = 1400, timestamp = getDaysAgo(now, 0) - 1 * 3600 * 1000)  // 1 hour ago
            )

            refillDao.insertLogs(initialLogs)
        }
    }

    private fun getDaysAgo(base: Calendar, days: Int): Long {
        val cal = base.clone() as Calendar
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return cal.timeInMillis
    }
}
