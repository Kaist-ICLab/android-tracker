package kaist.iclab.mobiletracker.services

import android.content.Context
import android.content.SharedPreferences
import kaist.iclab.mobiletracker.utils.DateTimeFormatter

/**
 * Service for tracking and retrieving sync-related timestamps.
 * Uses SharedPreferences for persistent storage of timestamps.
 */
class SyncTimestampService(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "sync_timestamps",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_LAST_WATCH_DATA = "last_watch_data"
        private const val KEY_LAST_PHONE_SENSOR = "last_phone_sensor"
        private const val KEY_LAST_SUCCESSFUL_UPLOAD = "last_successful_upload"
        private const val KEY_DATA_COLLECTION_STARTED = "data_collection_started"
        private const val KEY_NEXT_SCHEDULED_UPLOAD = "next_scheduled_upload"
    }

    /**
     * Update timestamp when watch data is received via BLE
     */
    fun updateLastWatchDataReceived() {
        prefs.edit().putLong(KEY_LAST_WATCH_DATA, System.currentTimeMillis()).apply()
    }

    /**
     * Update timestamp when phone sensor data is collected
     */
    fun updateLastPhoneSensorData() {
        prefs.edit().putLong(KEY_LAST_PHONE_SENSOR, System.currentTimeMillis()).apply()
    }

    /**
     * Update timestamp when data is successfully uploaded to server
     */
    fun updateLastSuccessfulUpload() {
        prefs.edit().putLong(KEY_LAST_SUCCESSFUL_UPLOAD, System.currentTimeMillis()).apply()
    }

    /**
     * Update timestamp when data collection starts
     */
    fun updateDataCollectionStarted() {
        prefs.edit().putLong(KEY_DATA_COLLECTION_STARTED, System.currentTimeMillis()).apply()
    }

    /**
     * Clear data collection started timestamp (when collection stops)
     */
    fun clearDataCollectionStarted() {
        prefs.edit().remove(KEY_DATA_COLLECTION_STARTED).apply()
    }

    /**
     * Set next scheduled upload time
     */
    fun setNextScheduledUpload(timestampMillis: Long?) {
        if (timestampMillis != null) {
            prefs.edit().putLong(KEY_NEXT_SCHEDULED_UPLOAD, timestampMillis).apply()
        } else {
            prefs.edit().remove(KEY_NEXT_SCHEDULED_UPLOAD).apply()
        }
    }

    /**
     * Get formatted last watch data received timestamp
     */
    fun getLastWatchDataReceived(): String? {
        val timestamp = prefs.getLong(KEY_LAST_WATCH_DATA, 0L)
        return if (timestamp > 0) {
            DateTimeFormatter.formatTimestampShort(timestamp)
        } else {
            null
        }
    }

    /**
     * Get formatted last phone sensor data timestamp
     */
    fun getLastPhoneSensorData(): String? {
        val timestamp = prefs.getLong(KEY_LAST_PHONE_SENSOR, 0L)
        return if (timestamp > 0) {
            DateTimeFormatter.formatTimestampShort(timestamp)
        } else {
            null
        }
    }

    /**
     * Get formatted last successful upload timestamp
     */
    fun getLastSuccessfulUpload(): String? {
        val timestamp = prefs.getLong(KEY_LAST_SUCCESSFUL_UPLOAD, 0L)
        return if (timestamp > 0) {
            DateTimeFormatter.formatTimestampShort(timestamp)
        } else {
            null
        }
    }

    /**
     * Get formatted data collection started timestamp
     */
    fun getDataCollectionStarted(): String? {
        val timestamp = prefs.getLong(KEY_DATA_COLLECTION_STARTED, 0L)
        return if (timestamp > 0) {
            DateTimeFormatter.formatTimestampShort(timestamp)
        } else {
            null
        }
    }

    /**
     * Get formatted next scheduled upload timestamp
     */
    fun getNextScheduledUpload(): String? {
        val timestamp = prefs.getLong(KEY_NEXT_SCHEDULED_UPLOAD, 0L)
        return if (timestamp > 0) {
            DateTimeFormatter.formatTimestampShort(timestamp)
        } else {
            null
        }
    }
}

