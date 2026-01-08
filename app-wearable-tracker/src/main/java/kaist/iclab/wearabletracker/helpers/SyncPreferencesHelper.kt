package kaist.iclab.wearabletracker.helpers

import android.content.Context
import android.content.SharedPreferences
import kaist.iclab.wearabletracker.data.SyncBatch

/**
 * Helper utility for managing sync metadata using SharedPreferences.
 * Provides a simple API for storing and retrieving sync state.
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
     * Save a pending sync batch (before confirmation is received).
     * This enables recovery if the app is killed during sync.
     */
    fun savePendingBatch(batch: SyncBatch) {
        sharedPreferences.edit()
            .putString(KEY_PENDING_BATCH_ID, batch.batchId)
            .putLong(KEY_PENDING_START_TS, batch.startTimestamp)
            .putLong(KEY_PENDING_END_TS, batch.endTimestamp)
            .putInt(KEY_PENDING_RECORD_COUNT, batch.recordCount)
            .putLong(KEY_PENDING_CREATED_AT, batch.createdAt)
            .apply()
    }

    /**
     * Get the pending sync batch, if one exists.
     * Returns null if there's no pending batch.
     */
    fun getPendingBatch(): SyncBatch? {
        val batchId = sharedPreferences.getString(KEY_PENDING_BATCH_ID, null) ?: return null
        return SyncBatch(
            batchId = batchId,
            startTimestamp = sharedPreferences.getLong(KEY_PENDING_START_TS, 0L),
            endTimestamp = sharedPreferences.getLong(KEY_PENDING_END_TS, 0L),
            recordCount = sharedPreferences.getInt(KEY_PENDING_RECORD_COUNT, 0),
            createdAt = sharedPreferences.getLong(KEY_PENDING_CREATED_AT, 0L)
        )
    }

    /**
     * Clear the pending batch after successful sync confirmation.
     */
    fun clearPendingBatch() {
        sharedPreferences.edit()
            .remove(KEY_PENDING_BATCH_ID)
            .remove(KEY_PENDING_START_TS)
            .remove(KEY_PENDING_END_TS)
            .remove(KEY_PENDING_RECORD_COUNT)
            .remove(KEY_PENDING_CREATED_AT)
            .apply()
    }

    /**
     * Check if there's a pending batch that hasn't been confirmed.
     */
    fun hasPendingBatch(): Boolean {
        return sharedPreferences.getString(KEY_PENDING_BATCH_ID, null) != null
    }

    companion object {
        private const val PREFS_NAME = "sync_preferences"
        private const val KEY_LAST_SYNC_TIMESTAMP = "last_sync_timestamp"
        private const val KEY_PENDING_BATCH_ID = "pending_batch_id"
        private const val KEY_PENDING_START_TS = "pending_start_timestamp"
        private const val KEY_PENDING_END_TS = "pending_end_timestamp"
        private const val KEY_PENDING_RECORD_COUNT = "pending_record_count"
        private const val KEY_PENDING_CREATED_AT = "pending_created_at"
    }
}
