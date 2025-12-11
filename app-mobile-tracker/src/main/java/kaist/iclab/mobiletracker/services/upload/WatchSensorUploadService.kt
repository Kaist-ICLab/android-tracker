package kaist.iclab.mobiletracker.services.upload

import android.util.Log
import kaist.iclab.mobiletracker.data.sensors.watch.*
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.*
import kaist.iclab.mobiletracker.db.mapper.*
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.SensorServiceRegistry
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.supabase.*
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper
import kotlinx.serialization.Serializable

/**
 * Service for uploading watch sensor data from Room database to Supabase.
 * Handles data retrieval, conversion, and upload for different watch sensor types.
 */
class WatchSensorUploadService(
    private val watchSensorDaos: Map<String, BaseDao<*, *>>,
    private val serviceRegistry: SensorServiceRegistry,
    private val supabaseHelper: SupabaseHelper,
    private val syncTimestampService: SyncTimestampService
) {
    companion object {
        private const val TAG = "WatchSensorUploadService"
        
        // Watch sensor IDs (matching string resource names)
        const val HEART_RATE_SENSOR_ID = "Heart Rate"
        const val ACCELEROMETER_SENSOR_ID = "Acceleration"
        const val EDA_SENSOR_ID = "EDA"
        const val PPG_SENSOR_ID = "PPG"
        const val SKIN_TEMPERATURE_SENSOR_ID = "Skin Temperature"
        const val LOCATION_SENSOR_ID = "Location"
    }

    /**
     * Upload watch sensor data to Supabase
     * @param sensorId The ID of the sensor to upload data for
     * @return Result indicating success or failure
     */
    suspend fun uploadSensorData(sensorId: String): Result<Unit> {
        return when (sensorId) {
            HEART_RATE_SENSOR_ID -> uploadHeartRateData(sensorId)
            ACCELEROMETER_SENSOR_ID -> uploadAccelerometerData(sensorId)
            EDA_SENSOR_ID -> uploadEDAData(sensorId)
            PPG_SENSOR_ID -> uploadPPGData(sensorId)
            SKIN_TEMPERATURE_SENSOR_ID -> uploadSkinTemperatureData(sensorId)
            LOCATION_SENSOR_ID -> uploadLocationData(sensorId)
            else -> {
                val error = UnsupportedOperationException("Upload not implemented for watch sensor: $sensorId")
                Log.w(TAG, error.message ?: "Unknown error")
                Result.Error(error)
            }
        }
    }

    private suspend fun uploadHeartRateData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = watchSensorDaos[sensorId] as? BaseDao<*, WatchHeartRateEntity>,
            mapper = HeartRateMapper,
            service = serviceRegistry.getService(sensorId) as? HeartRateSensorService,
            serviceName = "Heart Rate"
        )
    }

    private suspend fun uploadAccelerometerData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = watchSensorDaos[sensorId] as? BaseDao<*, WatchAccelerometerEntity>,
            mapper = AccelerometerMapper,
            service = serviceRegistry.getService(sensorId) as? AccelerometerSensorService,
            serviceName = "Accelerometer"
        )
    }

    private suspend fun uploadEDAData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = watchSensorDaos[sensorId] as? BaseDao<*, WatchEDAEntity>,
            mapper = EDAMapper,
            service = serviceRegistry.getService(sensorId) as? EDASensorService,
            serviceName = "EDA"
        )
    }

    private suspend fun uploadPPGData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = watchSensorDaos[sensorId] as? BaseDao<*, WatchPPGEntity>,
            mapper = PPGMapper,
            service = serviceRegistry.getService(sensorId) as? PPGSensorService,
            serviceName = "PPG"
        )
    }

    private suspend fun uploadSkinTemperatureData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = watchSensorDaos[sensorId] as? BaseDao<*, WatchSkinTemperatureEntity>,
            mapper = SkinTemperatureMapper,
            service = serviceRegistry.getService(sensorId) as? SkinTemperatureSensorService,
            serviceName = "Skin Temperature"
        )
    }

    private suspend fun uploadLocationData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = watchSensorDaos[sensorId] as? BaseDao<*, WatchLocationEntity>,
            mapper = LocationMapper,
            service = serviceRegistry.getService(sensorId) as? LocationSensorService,
            serviceName = "Location"
        )
    }

    /**
     * Generic upload method that handles the common upload pattern.
     */
    private suspend fun <TEntity, TSupabase : @Serializable Any> uploadData(
        sensorId: String,
        dao: BaseDao<*, TEntity>?,
        mapper: EntityToSupabaseMapper<TEntity, TSupabase>,
        service: BaseSupabaseService<TSupabase>?,
        serviceName: String
    ): Result<Unit> {
        return try {
            if (dao == null) {
                return Result.Error(IllegalStateException("DAO not found for sensor: $sensorId"))
            }
            if (service == null) {
                return Result.Error(IllegalStateException("Service not found for sensor: $sensorId"))
            }

            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            val entities = dao.getDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
            val supabaseDataList = entities.map { entity -> mapper.map(entity, userUuid) }

            @Suppress("UNCHECKED_CAST")
            val result = when (service) {
                is HeartRateSensorService -> service.insertHeartRateSensorDataBatch(supabaseDataList as List<HeartRateSensorData>)
                is AccelerometerSensorService -> service.insertAccelerometerSensorDataBatch(supabaseDataList as List<AccelerometerSensorData>)
                is EDASensorService -> service.insertEDASensorDataBatch(supabaseDataList as List<EDASensorData>)
                is PPGSensorService -> service.insertPPGSensorDataBatch(supabaseDataList as List<PPGSensorData>)
                is SkinTemperatureSensorService -> service.insertSkinTemperatureSensorDataBatch(supabaseDataList as List<SkinTemperatureSensorData>)
                is LocationSensorService -> service.insertLocationSensorDataBatch(supabaseDataList as List<LocationSensorData>)
                else -> Result.Error(IllegalStateException("Unsupported service type for sensor: $sensorId"))
            }

            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} $serviceName sensor entries")
            }

            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading $serviceName sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Check if there is data available to upload for a specific sensor
     */
    suspend fun hasDataToUpload(sensorId: String): Boolean {
        return try {
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            @Suppress("UNCHECKED_CAST")
            val dao = watchSensorDaos[sensorId] as? BaseDao<*, *>
            if (dao != null) {
                val entities = dao.getDataAfterTimestamp(lastUploadTimestamp)
                entities.isNotEmpty()
            } else {
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking data availability for sensor $sensorId: ${e.message}", e)
            false
        }
    }
}
