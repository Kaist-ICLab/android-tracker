package kaist.iclab.mobiletracker.repository

import android.util.Log
import kaist.iclab.mobiletracker.db.dao.BaseDao
import kaist.iclab.tracker.sensor.core.SensorEntity

/**
 * Implementation of PhoneSensorRepository using Room database DAOs.
 * Delegates to appropriate DAOs based on sensor ID.
 */
class PhoneSensorRepositoryImpl(
    private val sensorDataStorages: Map<String, BaseDao<*>>
) : PhoneSensorRepository {

    companion object {
        private const val TAG = "PhoneSensorRepository"
    }

    override suspend fun insertSensorData(sensorId: String, entity: SensorEntity): Boolean {
        return try {
            @Suppress("UNCHECKED_CAST")
            val dao = sensorDataStorages[sensorId] as? BaseDao<SensorEntity>
            if (dao != null) {
                dao.insert(entity)
                true
            } else {
                Log.w(TAG, "No DAO found for sensor ID: $sensorId")
                false
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Failed to insert sensor data for $sensorId: ${ex.message}", ex)
            false
        }
    }

    override suspend fun deleteAllSensorData(sensorId: String) {
        try {
            @Suppress("UNCHECKED_CAST")
            val dao = sensorDataStorages[sensorId] as? BaseDao<SensorEntity>
            if (dao != null) {
                dao.deleteAll()
            } else {
                Log.w(TAG, "No DAO found for sensor ID: $sensorId")
            }
        } catch (ex: Exception) {
            Log.e(TAG, "Failed to delete sensor data for $sensorId: ${ex.message}", ex)
        }
    }

    override fun hasStorageForSensor(sensorId: String): Boolean {
        return sensorDataStorages.containsKey(sensorId)
    }
}

