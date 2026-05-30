package com.example.data

import kotlinx.coroutines.flow.Flow

/**
 * Station Repository providing real-time local state.
 * Fully prepared for Future Supabase Migration where Smart node volumes, statuses,
 * and regional lat/long parameters are stored on PostgreSQL "Stations" table.
 */
class StationRepository(
    private val stationDao: StationDao
) {

    val allStations: Flow<List<Station>> = stationDao.getAllStations()

    suspend fun saveStation(station: Station) {
        stationDao.insertStation(station)
    }

    /**
     * Seeds initial campus stations if required.
     */
    suspend fun seedStationsIfEmpty(stations: List<Station>) {
        // Can be customized to pre-load campus stations in bulk
        for (st in stations) {
            stationDao.insertStation(st)
        }
    }
}
