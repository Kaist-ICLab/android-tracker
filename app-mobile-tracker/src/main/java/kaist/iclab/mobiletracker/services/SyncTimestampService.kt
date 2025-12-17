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

        // Automatic sync preferences
        private const val KEY_AUTO_SYNC_INTERVAL = "auto_sync_interval"
        private const val KEY_AUTO_SYNC_NETWORK = "auto_sync_network"

        // Interval values in minutes (0 = no auto sync)
        const val AUTO_SYNC_INTERVAL_NONE = 0
        const val AUTO_SYNC_INTERVAL_15_MIN = 15
        const val AUTO_SYNC_INTERVAL_30_MIN = 30
        const val AUTO_SYNC_INTERVAL_60_MIN = 60
        const val AUTO_SYNC_INTERVAL_120_MIN = 120

        // Network mode values
        const val AUTO_SYNC_NETWORK_WIFI_MOBILE = 0
        const val AUTO_SYNC_NETWORK_WIFI_ONLY = 1
        const val AUTO_SYNC_NETWORK_MOBILE_ONLY = 2
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
     * Update timestamp when data is successfully uploaded to server (global)
     */
    fun updateLastSuccessfulUpload() {
        prefs.edit().putLong(KEY_LAST_SUCCESSFUL_UPLOAD, System.currentTimeMillis()).apply()
    }

    /**
     * Update timestamp when a specific sensor's data is successfully uploaded to server
     * @param sensorId The ID of the sensor that was uploaded
     */
    fun updateLastSuccessfulUpload(sensorId: String) {
        val key = "last_upload_$sensorId"
        prefs.edit().putLong(key, System.currentTimeMillis()).apply()
        // Also update global timestamp
        updateLastSuccessfulUpload()
    }

    /**
     * Get the last successful upload timestamp for a specific sensor
     * @param sensorId The ID of the sensor
     * @return Formatted timestamp string, or null if never uploaded
     */
    fun getLastSuccessfulUpload(sensorId: String): String? {
        val key = "last_upload_$sensorId"
        val timestamp = prefs.getLong(key, 0L)
        return if (timestamp > 0) {
            DateTimeFormatter.formatTimestampShort(timestamp)
        } else {
            null
        }
    }

    /**
     * Get the last successful upload timestamp for a specific sensor (raw timestamp)
     * @param sensorId The ID of the sensor
     * @return Timestamp in milliseconds, or null if never uploaded
     */
    fun getLastSuccessfulUploadTimestamp(sensorId: String): Long? {
        val key = "last_upload_$sensorId"
        val timestamp = prefs.getLong(key, 0L)
        return if (timestamp > 0) timestamp else null
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
     * Automatic sync interval in minutes.
     * 0 means "No auto sync".
     */
    fun getAutoSyncIntervalMinutes(): Int {
        return prefs.getInt(KEY_AUTO_SYNC_INTERVAL, AUTO_SYNC_INTERVAL_NONE)
    }

    fun setAutoSyncIntervalMinutes(minutes: Int) {
        prefs.edit().putInt(KEY_AUTO_SYNC_INTERVAL, minutes).apply()
    }

    /**
     * Automatic sync network mode.
     * See AUTO_SYNC_NETWORK_* constants.
     */
    fun getAutoSyncNetworkMode(): Int {
        return prefs.getInt(KEY_AUTO_SYNC_NETWORK, AUTO_SYNC_NETWORK_WIFI_MOBILE)
    }

    fun setAutoSyncNetworkMode(mode: Int) {
        prefs.edit().putInt(KEY_AUTO_SYNC_NETWORK, mode).apply()
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

    /**
     * Clear the last successful upload timestamp for a specific sensor
     * @param sensorId The ID of the sensor
     */
    fun clearLastSuccessfulUpload(sensorId: String) {
        val key = "last_upload_$sensorId"
        prefs.edit().remove(key).apply()
    }

    /**
     * Clear all sensor upload timestamps
     */
    fun clearAllSensorUploadTimestamps() {
        val allKeys = prefs.all.keys
        val keysToRemove = allKeys.filter { it.startsWith("last_upload_") }
        val editor = prefs.edit()
        keysToRemove.forEach { editor.remove(it) }
        editor.apply()
    }

    /**
     * Clear all sync-related timestamps except next scheduled upload.
     * This includes:
     * - All per-sensor upload timestamps
     * - Global last successful upload
     * - Last watch data received
     * - Last phone sensor data
     * - Data collection started
     * 
     * Note: Does NOT clear next scheduled upload timestamp.
     */
    fun clearAllSyncTimestamps() {
        val editor = prefs.edit()
        
        // Clear all per-sensor upload timestamps
        clearAllSensorUploadTimestamps()
        
        // Clear global upload timestamp
        editor.remove(KEY_LAST_SUCCESSFUL_UPLOAD)
        
        // Clear last received timestamps
        editor.remove(KEY_LAST_WATCH_DATA)
        editor.remove(KEY_LAST_PHONE_SENSOR)
        
        // Clear data collection started
        editor.remove(KEY_DATA_COLLECTION_STARTED)
        
        // Note: KEY_NEXT_SCHEDULED_UPLOAD is intentionally NOT cleared
        editor.apply()
    }
}

