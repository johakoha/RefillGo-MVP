package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "refill_logs")
data class RefillLog(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val stationName: String,
    val amountMl: Int,
    val priceKrw: Int,
    val timestamp: Long,
    val userId: Int? = null,
    val paymentMethod: String = "KakaoPay",
    val status: String = "Completed"
)

@Entity(tableName = "users")
data class User(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val phoneNumber: String,
    val password: String,
    val createdAt: Long = System.currentTimeMillis(),
    val totalRefills: Int = 0,
    val totalVolumeRefilled: Int = 0,
    val totalBottlesSaved: Int = 0,
    val totalPlasticSaved: Double = 0.0,
    val totalCO2Prevented: Double = 0.0
)

@Entity(tableName = "stations")
data class Station(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val status: String,
    val availableVolume: Double,
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "impact_metrics")
data class ImpactMetrics(
    @androidx.room.PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val bottlesSaved: Int = 0,
    val plasticSaved: Double = 0.0,
    val co2Saved: Double = 0.0,
    val moneySaved: Double = 0.0
)

@Dao
interface RefillDao {
    @Query("SELECT * FROM refill_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<RefillLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: RefillLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLogs(logs: List<RefillLog>)

    @Query("DELETE FROM refill_logs")
    suspend fun clearAllLogs()

    @Query("SELECT COUNT(*) FROM refill_logs")
    suspend fun getCount(): Int

    @Query("SELECT * FROM refill_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getLogsForUser(userId: Int): Flow<List<RefillLog>>
}

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): User?

    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long

    @Query("UPDATE users SET totalRefills = totalRefills + 1, totalVolumeRefilled = totalVolumeRefilled + :volume, totalBottlesSaved = totalBottlesSaved + :bottles, totalPlasticSaved = totalPlasticSaved + :plastic, totalCO2Prevented = totalCO2Prevented + :co2 WHERE id = :id")
    suspend fun incrementUserStats(id: Int, volume: Int, bottles: Int, plastic: Double, co2: Double)

    @Query("UPDATE users SET totalRefills = :refills, totalVolumeRefilled = :volume, totalBottlesSaved = :bottles, totalPlasticSaved = :plastic, totalCO2Prevented = :co2 WHERE id = :id")
    suspend fun updateUserStatsDirect(id: Int, refills: Int, volume: Int, bottles: Int, plastic: Double, co2: Double)
}

@Dao
interface StationDao {
    @Query("SELECT * FROM stations")
    fun getAllStations(): Flow<List<Station>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: Station)
}

@Dao
interface ImpactMetricsDao {
    @Query("SELECT * FROM impact_metrics WHERE userId = :userId LIMIT 1")
    suspend fun getMetricsForUser(userId: Int): ImpactMetrics?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMetrics(metrics: ImpactMetrics)
}

@Database(entities = [RefillLog::class, User::class, Station::class, ImpactMetrics::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun refillDao(): RefillDao
    abstract fun userDao(): UserDao
    abstract fun stationDao(): StationDao
    abstract fun impactMetricsDao(): ImpactMetricsDao
}
