package kaist.iclab.mobiletracker.services

import android.os.BatteryManager
import android.util.Log
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BatterySensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BluetoothScanSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.ScreenSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.WifiSensorData
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.db.entity.BluetoothScanEntity
import kaist.iclab.mobiletracker.db.entity.ScreenEntity
import kaist.iclab.mobiletracker.db.entity.WifiEntity
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor
import kaist.iclab.tracker.sensor.phone.BatterySensor
import kaist.iclab.tracker.sensor.phone.BluetoothScanSensor
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
    private val screenSensorService: ScreenSensorService,
    private val wifiSensorService: WifiSensorService,
    private val supabaseHelper: SupabaseHelper
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
            is AmbientLightSensor -> uploadAmbientLightData()
            is BatterySensor -> uploadBatteryData()
            is BluetoothScanSensor -> uploadBluetoothScanData()
            is ScreenSensor -> uploadScreenData()
            is WifiScanSensor -> uploadWifiData()
            else -> {
                val error = UnsupportedOperationException("Upload not implemented for sensor: $sensorId")
                Log.w(TAG, error.message ?: "Unknown error")
                Result.Error(error)
            }
        }
    }

    /**
     * Upload ambient light sensor data to Supabase
     */
    private suspend fun uploadAmbientLightData(): Result<Unit> {
        return try {
            // Get all ambient light data from Room database
            val ambientLightDao = db.ambientLightDao()
            val entities = ambientLightDao.getAllAmbientLightData()

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertAmbientLightEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            ambientLightSensorService.insertAmbientLightSensorDataBatch(supabaseDataList)
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
     */
    private suspend fun uploadBatteryData(): Result<Unit> {
        return try {
            // Get all battery data from Room database
            val batteryDao = db.batteryDao()
            val entities = batteryDao.getAllBatteryData()

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertBatteryEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            batterySensorService.insertBatterySensorDataBatch(supabaseDataList)
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
     */
    private suspend fun uploadBluetoothScanData(): Result<Unit> {
        return try {
            // Get all Bluetooth scan data from Room database
            val bluetoothScanDao = db.bluetoothScanDao()
            val entities = bluetoothScanDao.getAllBluetoothScanData()

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertBluetoothScanEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            bluetoothScanSensorService.insertBluetoothScanSensorDataBatch(supabaseDataList)
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
     */
    private suspend fun uploadScreenData(): Result<Unit> {
        return try {
            // Get all screen data from Room database
            val screenDao = db.screenDao()
            val entities = screenDao.getAllScreenData()

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertScreenEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            screenSensorService.insertScreenSensorDataBatch(supabaseDataList)
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
     */
    private suspend fun uploadWifiData(): Result<Unit> {
        return try {
            // Get all WiFi data from Room database
            val wifiDao = db.wifiDao()
            val entities = wifiDao.getAllWifiData()

            if (entities.isEmpty()) {
                return Result.Error(IllegalStateException("No data available to upload"))
            }

            // Convert Room entities to Supabase data format
            val supabaseDataList = entities.map { entity ->
                convertWifiEntityToSupabaseData(entity)
            }

            // Upload to Supabase
            wifiSensorService.insertWifiSensorDataBatch(supabaseDataList)
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

