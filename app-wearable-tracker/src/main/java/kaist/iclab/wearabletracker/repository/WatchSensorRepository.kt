package kaist.iclab.wearabletracker.repository

/**
 * Repository interface for watch sensor data operations.
 * Abstracts data access from the ViewModel layer.
 */
interface WatchSensorRepository {
    /**
     * Delete all sensor data from local storage.
     */
    suspend fun deleteAllSensorData()

    /**
     * Get the last sync timestamp.
     * @return timestamp in milliseconds, or null if never synced
     */
    fun getLastSyncTimestamp(): Long?

    /**
     * Save the last sync timestamp.
     * @param timestamp timestamp in milliseconds
     */
    fun saveLastSyncTimestamp(timestamp: Long)
}
