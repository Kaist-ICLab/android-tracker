package kaist.iclab.mobiletracker.services

import android.os.BatteryManager
import android.util.Log
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BatterySensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BluetoothScanSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DataTrafficSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.DeviceModeSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.ScreenSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.WifiSensorData
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.DataTrafficEntity
import kaist.iclab.mobiletracker.db.entity.DeviceModeEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.WifiEntity
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor
import kaist.iclab.tracker.sensor.phone.BatterySensor
import kaist.iclab.tracker.sensor.phone.BluetoothScanSensor
import kaist.iclab.tracker.sensor.phone.DataTrafficStatSensor
import kaist.iclab.tracker.sensor.phone.DeviceModeSensor
import kaist.iclab.tracker.sensor.phone.ScreenSensor
import kaist.iclab.tracker.sensor.phone.WifiScanSensor

/**
 * Service for uploading phone sensor data from Room database to Supabase.
 * Handles data retrieval, conversion, and upload for different sensor types.
 */
class PhoneSensorUploadService(
    private val db: TrackerRoomDB,
    private val ambientLightSensorService: AmbientLightSensorService,
    private val batterySensorService: BatterySensorService,
    private val bluetoothScanSensorService: BluetoothScanSensorService,
    private val dataTrafficSensorService: DataTrafficSensorService,
    private val deviceModeSensorService: DeviceModeSensorService,
    private val screenSensorService: ScreenSensorService,
    private val wifiSensorService: WifiSensorService,
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

    /**
     * Upload ambient light sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadAmbientLightData(sensorId: String): Result<Unit> {
        return try {
            val ambientLightDao = db.ambientLightDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = ambientLightDao.getAmbientLightDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertAmbientLightEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = ambientLightSensorService.insertAmbientLightSensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                val latestTimestamp = entities.maxOfOrNull { it.timestamp } ?: lastUploadTimestamp
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading ambient light sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for Ambient Light sensor
     */
    private fun convertAmbientLightEntityToSupabaseData(entity: AmbientLightEntity): AmbientLightSensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        return AmbientLightSensorData(
            uuid = userUuid,
            timestamp = timestampString,
            value = entity.value,
            received = entity.received,
            accuracy = entity.accuracy
        )
    }

    /**
     * Upload battery sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadBatteryData(sensorId: String): Result<Unit> {
        return try {
            val batteryDao = db.batteryDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = batteryDao.getBatteryDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertBatteryEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = batterySensorService.insertBatterySensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} battery sensor entries")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading battery sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for Battery sensor
     */
    private fun convertBatteryEntityToSupabaseData(entity: BatteryEntity): BatterySensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        // Convert connectedType (Int) to plugged (String)
        val plugged = when (entity.connectedType) {
            BatteryManager.BATTERY_PLUGGED_AC -> "AC"
            BatteryManager.BATTERY_PLUGGED_USB -> "USB"
            BatteryManager.BATTERY_PLUGGED_WIRELESS -> "WIRELESS"
            else -> "UNPLUGGED"
        }
        
        // Convert status (Int) to status (String)
        val status = when (entity.status) {
            BatteryManager.BATTERY_STATUS_CHARGING -> "charging"
            BatteryManager.BATTERY_STATUS_DISCHARGING -> "discharging"
            BatteryManager.BATTERY_STATUS_FULL -> "full"
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> "not_charging"
            else -> "unknown"
        }
        
        return BatterySensorData(
            uuid = userUuid,
            timestamp = timestampString,
            level = entity.level.toFloat(),
            plugged = plugged,
            status = status,
            temperature = entity.temperature
        )
    }

    /**
     * Upload Bluetooth scan sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadBluetoothScanData(sensorId: String): Result<Unit> {
        return try {
            val bluetoothScanDao = db.bluetoothScanDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = bluetoothScanDao.getBluetoothScanDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertBluetoothScanEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = bluetoothScanSensorService.insertBluetoothScanSensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} Bluetooth scan sensor entries")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading Bluetooth scan sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for Bluetooth Scan sensor
     */
    private fun convertBluetoothScanEntityToSupabaseData(entity: BluetoothScanEntity): BluetoothScanSensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        return BluetoothScanSensorData(
            uuid = userUuid,
            timestamp = timestampString,
            name = entity.name,
            alias = entity.alias,
            address = entity.address,
            bondState = entity.bondState,
            connectionType = entity.connectionType,
            classType = entity.classType,
            rssi = entity.rssi,
            isLE = entity.isLE
        )
    }

    /**
     * Upload screen sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadScreenData(sensorId: String): Result<Unit> {
        return try {
            val screenDao = db.screenDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = screenDao.getScreenDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertScreenEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = screenSensorService.insertScreenSensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} screen sensor entries")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading screen sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for Screen sensor
     */
    private fun convertScreenEntityToSupabaseData(entity: ScreenEntity): ScreenSensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        return ScreenSensorData(
            uuid = userUuid,
            timestamp = timestampString,
            type = entity.type,
            received = entity.received
        )
    }

    /**
     * Upload WiFi sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadWifiData(sensorId: String): Result<Unit> {
        return try {
            val wifiDao = db.wifiDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = wifiDao.getWifiDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertWifiEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = wifiSensorService.insertWifiSensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} WiFi sensor entries")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading WiFi sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for WiFi sensor
     */
    private fun convertWifiEntityToSupabaseData(entity: WifiEntity): WifiSensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        return WifiSensorData(
            uuid = userUuid,
            timestamp = timestampString,
            bssid = entity.bssid,
            frequency = entity.frequency,
            rssi = entity.level, // level in Entity is rssi in Supabase data
            ssid = entity.ssid,
            received = entity.received
        )
    }

    /**
     * Upload Data Traffic sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadDataTrafficData(sensorId: String): Result<Unit> {
        return try {
            val dataTrafficDao = db.dataTrafficDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = dataTrafficDao.getDataTrafficDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertDataTrafficEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = dataTrafficSensorService.insertDataTrafficSensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} Data Traffic sensor entries")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading Data Traffic sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for Data Traffic sensor
     */
    private fun convertDataTrafficEntityToSupabaseData(entity: DataTrafficEntity): DataTrafficSensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        return DataTrafficSensorData(
            uuid = userUuid,
            timestamp = timestampString,
            totalRx = entity.totalRx,
            totalTx = entity.totalTx,
            mobileRx = entity.mobileRx,
            mobileTx = entity.mobileTx,
            received = entity.received
        )
    }

    /**
     * Upload Device Mode sensor data to Supabase
     * Only uploads data that hasn't been uploaded before (based on timestamp)
     */
    private suspend fun uploadDeviceModeData(sensorId: String): Result<Unit> {
        return try {
            val deviceModeDao = db.deviceModeDao()
            
            // Get the last upload timestamp for this sensor
            val lastUploadTimestamp = syncTimestampService.getLastSuccessfulUploadTimestamp(sensorId) ?: 0L
            
            // Get only data created after the last upload timestamp
            val entities = deviceModeDao.getDeviceModeDataAfterTimestamp(lastUploadTimestamp)

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No new data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertDeviceModeEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            val result = deviceModeSensorService.insertDeviceModeSensorDataBatch(supabaseDataList)
            
            // If upload successful, update the last upload timestamp
            if (result is Result.Success) {
                syncTimestampService.updateLastSuccessfulUpload(sensorId)
                Log.d(TAG, "Successfully uploaded ${entities.size} Device Mode sensor entries")
            }
            
            result
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading Device Mode sensor data: ${e.message}", e)
            Result.Error(e)
        }
    }

    /**
     * Convert Room entity to Supabase data format for Device Mode sensor
     */
    private fun convertDeviceModeEntityToSupabaseData(entity: DeviceModeEntity): DeviceModeSensorData {
        // Get user UUID from Supabase session
        val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient)
        
        // Convert timestamp from milliseconds to "YYYY-MM-DD HH:mm:ss" format
        val timestampString = DateTimeFormatter.formatTimestamp(entity.timestamp)
        
        return DeviceModeSensorData(
            uuid = userUuid,
            received = entity.received,
            timestamp = timestampString,
            eventType = entity.eventType,
            value = entity.value
        )
    }

    /**
     * Check if a sensor has data available to upload
     * @param sensorId The sensor ID to check
     * @param sensor The sensor instance
     * @return true if data is available, false otherwise
     */
    suspend fun hasDataToUpload(sensorId: String, sensor: Sensor<*, *>): Boolean {
        return when (sensor) {
            is AmbientLightSensor -> {
                val ambientLightDao = db.ambientLightDao()
                val entities = ambientLightDao.getAllAmbientLightData()
                entities.isNotEmpty()
            }
            is BatterySensor -> {
                val batteryDao = db.batteryDao()
                val entities = batteryDao.getAllBatteryData()
                entities.isNotEmpty()
            }
            is BluetoothScanSensor -> {
                val bluetoothScanDao = db.bluetoothScanDao()
                val entities = bluetoothScanDao.getAllBluetoothScanData()
                entities.isNotEmpty()
            }
            is DataTrafficStatSensor -> {
                val dataTrafficDao = db.dataTrafficDao()
                val entities = dataTrafficDao.getAllDataTrafficData()
                entities.isNotEmpty()
            }
            is DeviceModeSensor -> {
                val deviceModeDao = db.deviceModeDao()
                val entities = deviceModeDao.getAllDeviceModeData()
                entities.isNotEmpty()
            }
            is ScreenSensor -> {
                val screenDao = db.screenDao()
                val entities = screenDao.getAllScreenData()
                entities.isNotEmpty()
            }
            is WifiScanSensor -> {
                val wifiDao = db.wifiDao()
                val entities = wifiDao.getAllWifiData()
                entities.isNotEmpty()
            }
            else -> false
        }
    }
}

