package kaist.iclab.wearabletracker.helpers

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper utility for managing sync metadata using SharedPreferences.
 * Provides a simple API for storing and retrieving the last successful sync timestamp.
 */
class SyncPreferencesHelper(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    /**
     * Save the last successful sync timestamp.
     */
    fun saveLastSyncTimestamp(timestamp: Long) {
        sharedPreferences.edit()
            .putLong(KEY_LAST_SYNC_TIMESTAMP, timestamp)
            .apply()
    }

    /**
     * Get the last successful sync timestamp.
     * Returns null if no sync has been recorded yet.
     */
    fun getLastSyncTimestamp(): Long? {
        val timestamp = sharedPreferences.getLong(KEY_LAST_SYNC_TIMESTAMP, -1L)
        return if (timestamp == -1L) null else timestamp
    }

    /**
     * Clear the last sync timestamp.
     */
    fun clearLastSyncTimestamp() {
        sharedPreferences.edit()
            .remove(KEY_LAST_SYNC_TIMESTAMP)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "sync_preferences"
        private const val KEY_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
    }
}

