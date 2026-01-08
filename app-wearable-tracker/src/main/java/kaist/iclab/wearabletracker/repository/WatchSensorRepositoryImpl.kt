package kaist.iclab.wearabletracker.repository

import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.helpers.SyncPreferencesHelper

/**
 * Implementation of WatchSensorRepository.
 * Handles data operations using DAOs and SyncPreferencesHelper.
 */
class WatchSensorRepositoryImpl(
    private val sensorDataStorages: Map<String, BaseDao<*>>,
    private val syncPreferencesHelper: SyncPreferencesHelper
) : WatchSensorRepository {

    override suspend fun deleteAllSensorData() {
        sensorDataStorages.values.forEach { it.deleteAll() }
    }

    override fun getLastSyncTimestamp(): Long? {
        return syncPreferencesHelper.getLastSyncTimestamp()
    }

    override fun saveLastSyncTimestamp(timestamp: Long) {
        syncPreferencesHelper.saveLastSyncTimestamp(timestamp)
    }
}
