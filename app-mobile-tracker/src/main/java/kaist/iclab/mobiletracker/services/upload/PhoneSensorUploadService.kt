package kaist.iclab.mobiletracker.services.upload

import android.util.Log
import kaist.iclab.mobiletracker.data.sensors.phone.*
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.*
import kaist.iclab.mobiletracker.db.mapper.*
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.SensorServiceRegistry
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.supabase.*
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.phone.*
import kotlinx.serialization.Serializable

/**
 * Service for uploading phone sensor data from Room database to Supabase.
 * Handles data retrieval, conversion, and upload for different sensor types.
 * Uses DAO map pattern for better abstraction, consistent with WatchSensorUploadService.
 */
class PhoneSensorUploadService(
    private val phoneSensorDaos: Map<String, BaseDao<*, *>>,
    private val serviceRegistry: SensorServiceRegistry,
    private val supabaseHelper: SupabaseHelper,
    private val syncTimestampService: SyncTimestampService
) {
    companion object {
        private const val TAG = "PhoneSensorUploadService"
    }

    /**
     * Upload sensor data to Supabase
     * @param sensorId The ID of the sensor to upload data for
     * @param sensor The sensor instance (used to determine sensor type)
     * @return Result indicating success or failure
     */
    suspend fun uploadSensorData(sensorId: String, sensor: Sensor<*, *>): Result<Unit> {
        return when (sensor) {
            is AmbientLightSensor -> uploadAmbientLightData(sensorId)
            is BatterySensor -> uploadBatteryData(sensorId)
            is BluetoothScanSensor -> uploadBluetoothScanData(sensorId)
            is DataTrafficStatSensor -> uploadDataTrafficData(sensorId)
            is DeviceModeSensor -> uploadDeviceModeData(sensorId)
            is ScreenSensor -> uploadScreenData(sensorId)
            is WifiScanSensor -> uploadWifiData(sensorId)
            else -> {
                val error = UnsupportedOperationException("Upload not implemented for sensor: $sensorId")
                Log.w(TAG, error.message ?: "Unknown error")
                Result.Error(error)
            }
        }
    }

    private suspend fun uploadAmbientLightData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, AmbientLightEntity>,
            mapper = AmbientLightMapper,
            service = serviceRegistry.getService(sensorId) as? AmbientLightSensorService,
            serviceName = "Ambient Light"
        )
    }

    private suspend fun uploadBatteryData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, BatteryEntity>,
            mapper = BatteryMapper,
            service = serviceRegistry.getService(sensorId) as? BatterySensorService,
            serviceName = "Battery"
        )
    }

    private suspend fun uploadBluetoothScanData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, BluetoothScanEntity>,
            mapper = BluetoothScanMapper,
            service = serviceRegistry.getService(sensorId) as? BluetoothScanSensorService,
            serviceName = "Bluetooth Scan"
        )
    }

    private suspend fun uploadDataTrafficData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, DataTrafficEntity>,
            mapper = DataTrafficMapper,
            service = serviceRegistry.getService(sensorId) as? DataTrafficSensorService,
            serviceName = "Data Traffic"
        )
    }

    private suspend fun uploadDeviceModeData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, DeviceModeEntity>,
            mapper = DeviceModeMapper,
            service = serviceRegistry.getService(sensorId) as? DeviceModeSensorService,
            serviceName = "Device Mode"
        )
    }

    private suspend fun uploadScreenData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, ScreenEntity>,
            mapper = ScreenMapper,
            service = serviceRegistry.getService(sensorId) as? ScreenSensorService,
            serviceName = "Screen"
        )
    }

    private suspend fun uploadWifiData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, WifiEntity>,
            mapper = WifiMapper,
            service = serviceRegistry.getService(sensorId) as? WifiSensorService,
            serviceName = "WiFi"
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
                is AmbientLightSensorService -> service.insertAmbientLightSensorDataBatch(supabaseDataList as List<AmbientLightSensorData>)
                is BatterySensorService -> service.insertBatterySensorDataBatch(supabaseDataList as List<BatterySensorData>)
                is BluetoothScanSensorService -> service.insertBluetoothScanSensorDataBatch(supabaseDataList as List<BluetoothScanSensorData>)
                is DataTrafficSensorService -> service.insertDataTrafficSensorDataBatch(supabaseDataList as List<DataTrafficSensorData>)
                is DeviceModeSensorService -> service.insertDeviceModeSensorDataBatch(supabaseDataList as List<DeviceModeSensorData>)
                is ScreenSensorService -> service.insertScreenSensorDataBatch(supabaseDataList as List<ScreenSensorData>)
                is WifiSensorService -> service.insertWifiSensorDataBatch(supabaseDataList as List<WifiSensorData>)
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
    suspend fun hasDataToUpload(sensorId: String, sensor: Sensor<*, *>): Boolean {
        return try {
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            @Suppress("UNCHECKED_CAST")
            val dao = phoneSensorDaos[sensorId] as? BaseDao<*, *>
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
