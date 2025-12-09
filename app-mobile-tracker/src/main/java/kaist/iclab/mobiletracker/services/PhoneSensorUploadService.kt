package kaist.iclab.mobiletracker.services

import android.os.BatteryManager
import android.util.Log
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.data.sensors.phone.BatterySensorData
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.entity.AmbientLightEntity
import kaist.iclab.mobiletracker.db.entity.BatteryEntity
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.phone.AmbientLightSensor
import kaist.iclab.tracker.sensor.phone.BatterySensor

/**
 * Service for uploading phone sensor data from Room database to Supabase.
 * Handles data retrieval, conversion, and upload for different sensor types.
 */
class PhoneSensorUploadService(
    private val db: TrackerRoomDB,
    private val ambientLightSensorService: AmbientLightSensorService,
    private val batterySensorService: BatterySensorService,
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
            else -> false
        }
    }
}

