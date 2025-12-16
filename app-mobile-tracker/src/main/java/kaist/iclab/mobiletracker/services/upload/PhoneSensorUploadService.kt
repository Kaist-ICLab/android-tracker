package kaist.iclab.mobiletracker.services.upload

import android.util.Log
import kaist.iclab.mobiletracker.data.DeviceType
import kaist.iclab.mobiletracker.data.sensors.common.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.*
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.dao.common.LocationDao
import kaist.iclab.mobiletracker.db.entity.phone.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.phone.AppListChangeEntity
import kaist.iclab.mobiletracker.db.entity.phone.AppUsageLogEntity
import kaist.iclab.mobiletracker.db.entity.phone.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.phone.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.phone.CallLogEntity
import kaist.iclab.mobiletracker.db.entity.phone.ConnectivityEntity
import kaist.iclab.mobiletracker.db.entity.phone.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.phone.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.phone.MediaEntity
import kaist.iclab.mobiletracker.db.entity.phone.MessageLogEntity
import kaist.iclab.mobiletracker.db.entity.phone.StepEntity
import kaist.iclab.mobiletracker.db.entity.phone.UserInteractionEntity
import kaist.iclab.mobiletracker.db.entity.phone.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.phone.WifiScanEntity
import kaist.iclab.mobiletracker.db.mapper.*
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.SensorServiceRegistry
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.supabase.*
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper
import kaist.iclab.tracker.sensor.common.LocationSensor
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
            is AppListChangeSensor -> uploadAppListChangeData(sensorId)
            is AppUsageLogSensor -> uploadAppUsageLogData(sensorId)
            is BatterySensor -> uploadBatteryData(sensorId)
            is BluetoothScanSensor -> uploadBluetoothScanData(sensorId)
            is CallLogSensor -> uploadCallLogData(sensorId)
            is ConnectivitySensor -> uploadConnectivityData(sensorId)
            is MessageLogSensor -> uploadMessageLogData(sensorId)
            is MediaSensor -> uploadMediaData(sensorId)
            is StepSensor -> uploadStepData(sensorId)
            is UserInteractionSensor -> uploadUserInteractionData(sensorId)
            is DataTrafficSensor -> uploadDataTrafficData(sensorId)
            is DeviceModeSensor -> uploadDeviceModeData(sensorId)
            is LocationSensor -> uploadPhoneLocationData(sensorId)
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

    private suspend fun uploadAppListChangeData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, AppListChangeEntity>,
            mapper = AppListChangeMapper,
            service = serviceRegistry.getService(sensorId) as? AppListChangeSensorService,
            serviceName = "App List Change"
        )
    }

    private suspend fun uploadAppUsageLogData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, AppUsageLogEntity>,
            mapper = AppUsageLogMapper,
            service = serviceRegistry.getService(sensorId) as? AppUsageLogSensorService,
            serviceName = "App Usage Log"
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

    private suspend fun uploadCallLogData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, CallLogEntity>,
            mapper = CallLogMapper,
            service = serviceRegistry.getService(sensorId) as? CallLogSensorService,
            serviceName = "Call Log"
        )
    }

    private suspend fun uploadConnectivityData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, ConnectivityEntity>,
            mapper = ConnectivityMapper,
            service = serviceRegistry.getService(sensorId) as? ConnectivitySensorService,
            serviceName = "Connectivity"
        )
    }

    private suspend fun uploadMessageLogData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, MessageLogEntity>,
            mapper = MessageLogMapper,
            service = serviceRegistry.getService(sensorId) as? MessageLogSensorService,
            serviceName = "Message Log"
        )
    }

    private suspend fun uploadMediaData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, MediaEntity>,
            mapper = MediaMapper,
            service = serviceRegistry.getService(sensorId) as? MediaSensorService,
            serviceName = "Media"
        )
    }

    private suspend fun uploadStepData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, StepEntity>,
            mapper = StepMapper,
            service = serviceRegistry.getService(sensorId) as? StepSensorService,
            serviceName = "Step"
        )
    }

    private suspend fun uploadUserInteractionData(sensorId: String): Result<Unit> {
        return uploadData(
            sensorId = sensorId,
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, UserInteractionEntity>,
            mapper = UserInteractionMapper,
            service = serviceRegistry.getService(sensorId) as? UserInteractionSensorService,
            serviceName = "User Interaction"
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

    private suspend fun uploadPhoneLocationData(sensorId: String): Result<Unit> {
        val dao = phoneSensorDaos[sensorId] as? LocationDao
        return uploadData(
            sensorId = sensorId,
            dao = dao,
            mapper = PhoneLocationMapper,
            service = serviceRegistry.getService(sensorId) as? LocationSensorService,
            serviceName = "Phone Location",
            customQuery = { timestamp ->
                // Filter by deviceType = PHONE to only get phone location data
                dao?.getDataAfterTimestampByDeviceType(timestamp, deviceType = DeviceType.PHONE.value) ?: emptyList()
            }
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
            dao = phoneSensorDaos[sensorId] as? BaseDao<*, WifiScanEntity>,
            mapper = WifiMapper,
            service = serviceRegistry.getService(sensorId) as? WifiSensorService,
            serviceName = "WiFi"
        )
    }

    /**
     * Generic upload method that handles the common upload pattern.
     * @param customQuery Optional custom query function to override the default getDataAfterTimestamp behavior
     */
    private suspend fun <TEntity, TSupabase : @Serializable Any> uploadData(
        sensorId: String,
        dao: BaseDao<*, TEntity>?,
        mapper: EntityToSupabaseMapper<TEntity, TSupabase>,
        service: BaseSupabaseService<TSupabase>?,
        serviceName: String,
        customQuery: (suspend (Long) -> List<TEntity>)? = null
    ): Result<Unit> {
        return try {
            if (dao == null) {
                return Result.Error(IllegalStateException("DAO not found for sensor: $sensorId"))
            }
            if (service == null) {
                return Result.Error(IllegalStateException("Service not found for sensor: $sensorId"))
            }

            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            val entities = customQuery?.invoke(lastUploadTimestamp) ?: dao.getDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
            val supabaseDataList = entities.map { entity -> mapper.map(entity, userUuid) }

            @Suppress("UNCHECKED_CAST")
            val result = when (service) {
                is AmbientLightSensorService -> service.insertAmbientLightSensorDataBatch(supabaseDataList as List<AmbientLightSensorData>)
                is AppListChangeSensorService -> service.insertAppListChangeSensorDataBatch(supabaseDataList as List<AppListChangeSensorData>)
                is AppUsageLogSensorService -> service.insertAppUsageLogSensorDataBatch(supabaseDataList as List<AppUsageLogSensorData>)
                is BatterySensorService -> service.insertBatterySensorDataBatch(supabaseDataList as List<BatterySensorData>)
                is BluetoothScanSensorService -> service.insertBluetoothScanSensorDataBatch(supabaseDataList as List<BluetoothScanSensorData>)
                is CallLogSensorService -> service.insertCallLogSensorDataBatch(supabaseDataList as List<CallLogSensorData>)
                is ConnectivitySensorService -> service.insertConnectivitySensorDataBatch(supabaseDataList as List<ConnectivitySensorData>)
                is MessageLogSensorService -> service.insertMessageLogSensorDataBatch(supabaseDataList as List<MessageLogSensorData>)
                is MediaSensorService -> service.insertMediaSensorDataBatch(supabaseDataList as List<MediaSensorData>)
                is StepSensorService -> service.insertStepSensorDataBatch(supabaseDataList as List<StepSensorData>)
                is UserInteractionSensorService -> service.insertUserInteractionSensorDataBatch(supabaseDataList as List<UserInteractionSensorData>)
                is DataTrafficSensorService -> service.insertDataTrafficSensorDataBatch(supabaseDataList as List<DataTrafficSensorData>)
                is DeviceModeSensorService -> service.insertDeviceModeSensorDataBatch(supabaseDataList as List<DeviceModeSensorData>)
                is LocationSensorService -> service.insertLocationSensorDataBatch(supabaseDataList as List<LocationSensorData>)
                is ScreenSensorService -> service.insertScreenSensorDataBatch(supabaseDataList as List<ScreenSensorData>)
                is WifiSensorService -> service.insertWifiSensorDataBatch(supabaseDataList as List<WifiScanSensorData>)
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
