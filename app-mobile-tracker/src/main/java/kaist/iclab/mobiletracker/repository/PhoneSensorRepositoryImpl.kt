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

    override suspend fun insertSensorData(sensorId: String, entity: SensorEntity): Result<Unit> {
        return runCatchingSuspend {
            @Suppress("UNCHECKED_CAST")
            val dao = sensorDataStorages[sensorId] as? BaseDao<SensorEntity>
            if (dao != null) {
                dao.insert(entity)
            } else {
                val error = IllegalStateException("No DAO found for sensor ID: $sensorId")
                Log.w(TAG, error.message ?: "Unknown error")
                throw error
            }
        }
    }

    override suspend fun deleteAllSensorData(sensorId: String): Result<Unit> {
        return runCatchingSuspend {
            @Suppress("UNCHECKED_CAST")
            val dao = sensorDataStorages[sensorId] as? BaseDao<SensorEntity>
            if (dao != null) {
                dao.deleteAll()
            } else {
                val error = IllegalStateException("No DAO found for sensor ID: $sensorId")
                Log.w(TAG, error.message ?: "Unknown error")
                throw error
            }
        }
    }

    override fun hasStorageForSensor(sensorId: String): Boolean {
        return sensorDataStorages.containsKey(sensorId)
    }
}

