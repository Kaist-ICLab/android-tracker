package kaist.iclab.mobiletracker.helpers

import android.content.Context
import android.util.Log
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.db.entity.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.WatchLocationEntity
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.repository.WatchSensorRepository
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kaist.iclab.mobiletracker.utils.SensorDataCsvParser
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Helper class for managing BLE communication with wearable devices.
 * Handles receiving sensor data via BLE and storing it locally in Room database.
 */
class BLEHelper(
    private val context: Context,
    private val watchSensorRepository: WatchSensorRepository
) {
    private val timestampService = kaist.iclab.mobiletracker.services.SyncTimestampService(context)
    private lateinit var bleChannel: BLEDataChannel
    
    // Create a managed coroutine scope that can be cancelled
    private val ioScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    fun initialize() {
        bleChannel = BLEDataChannel(context)
        setupListeners()
    }
    
    /**
     * Cleanup method to cancel all coroutines when BLEHelper is no longer needed.
     * Should be called when the helper is being destroyed.
     */
    fun cleanup() {
        ioScope.cancel()
    }

    private fun setupListeners() {
        // Listen for sensor CSV data from watch
        bleChannel.addOnReceivedListener(setOf(AppConfig.BLEKeys.SENSOR_DATA_CSV)) { _, json ->
            val csvData = when {
                json is kotlinx.serialization.json.JsonPrimitive -> json.content
                else -> json.toString()
            }
            // Parse CSV and store all sensor data locally
            parseAndStoreWatchData(csvData)
        }
    }

    /**
     * Parse CSV data and extract all sensor data types, then store locally in Room database.
     * Uses managed coroutine scope for proper lifecycle management.
     */
    private fun parseAndStoreWatchData(csvData: String) {
        ioScope.launch {
            try {
                val currentTime = System.currentTimeMillis()
                
                // Parse all sensor types from CSV
                val locationDataList = SensorDataCsvParser.parseLocationCsv(csvData)
                val accelerometerDataList = SensorDataCsvParser.parseAccelerometerCsv(csvData)
                val edaDataList = SensorDataCsvParser.parseEDACsv(csvData)
                val heartRateDataList = SensorDataCsvParser.parseHeartRateCsv(csvData)
                val ppgDataList = SensorDataCsvParser.parsePPGCsv(csvData)
                val skinTemperatureDataList = SensorDataCsvParser.parseSkinTemperatureCsv(csvData)
                
                // Convert Supabase data classes to Room entities and store locally
                var totalStored = 0
                var hasAnyData = false
                
                if (locationDataList.isNotEmpty()) {
                    hasAnyData = true
                    val entities = locationDataList.map { data ->
                        // Parse timestamp from "YYYY-MM-DD HH:mm:ss" back to milliseconds
                        val timestampMillis = DateTimeFormatter.parseTimestamp(data.timestamp)
                        WatchLocationEntity(
                            received = currentTime,
                            timestamp = timestampMillis,
                            latitude = data.latitude,
                            longitude = data.longitude,
                            altitude = data.altitude,
                            speed = data.speed,
                            accuracy = data.accuracy
                        )
                    }
                    when (val result = watchSensorRepository.insertLocationData(entities)) {
                        is Result.Success -> {
                            totalStored += entities.size
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Stored ${entities.size} location entries locally")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to store location data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (accelerometerDataList.isNotEmpty()) {
                    hasAnyData = true
                    val entities = accelerometerDataList.map { data ->
                        val timestampMillis = DateTimeFormatter.parseTimestamp(data.timestamp)
                        WatchAccelerometerEntity(
                            received = currentTime,
                            timestamp = timestampMillis,
                            x = data.x,
                            y = data.y,
                            z = data.z
                        )
                    }
                    when (val result = watchSensorRepository.insertAccelerometerData(entities)) {
                        is Result.Success -> {
                            totalStored += entities.size
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Stored ${entities.size} accelerometer entries locally")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to store accelerometer data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (edaDataList.isNotEmpty()) {
                    hasAnyData = true
                    val entities = edaDataList.map { data ->
                        val timestampMillis = DateTimeFormatter.parseTimestamp(data.timestamp)
                        WatchEDAEntity(
                            received = currentTime,
                            timestamp = timestampMillis,
                            skinConductance = data.skinConductance,
                            status = data.status
                        )
                    }
                    when (val result = watchSensorRepository.insertEDAData(entities)) {
                        is Result.Success -> {
                            totalStored += entities.size
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Stored ${entities.size} EDA entries locally")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to store EDA data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (heartRateDataList.isNotEmpty()) {
                    hasAnyData = true
                    val entities = heartRateDataList.map { data ->
                        val timestampMillis = DateTimeFormatter.parseTimestamp(data.timestamp)
                        WatchHeartRateEntity(
                            received = currentTime,
                            timestamp = timestampMillis,
                            hr = data.hr,
                            hrStatus = data.hrStatus,
                            ibi = data.ibi,
                            ibiStatus = data.ibiStatus
                        )
                    }
                    when (val result = watchSensorRepository.insertHeartRateData(entities)) {
                        is Result.Success -> {
                            totalStored += entities.size
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Stored ${entities.size} heart rate entries locally")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to store heart rate data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (ppgDataList.isNotEmpty()) {
                    hasAnyData = true
                    val entities = ppgDataList.map { data ->
                        val timestampMillis = DateTimeFormatter.parseTimestamp(data.timestamp)
                        WatchPPGEntity(
                            received = currentTime,
                            timestamp = timestampMillis,
                            green = data.green,
                            greenStatus = data.greenStatus,
                            red = data.red,
                            redStatus = data.redStatus,
                            ir = data.ir,
                            irStatus = data.irStatus
                        )
                    }
                    when (val result = watchSensorRepository.insertPPGData(entities)) {
                        is Result.Success -> {
                            totalStored += entities.size
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Stored ${entities.size} PPG entries locally")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to store PPG data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (skinTemperatureDataList.isNotEmpty()) {
                    hasAnyData = true
                    val entities = skinTemperatureDataList.map { data ->
                        val timestampMillis = DateTimeFormatter.parseTimestamp(data.timestamp)
                        WatchSkinTemperatureEntity(
                            received = currentTime,
                            timestamp = timestampMillis,
                            ambientTemp = data.ambientTemp,
                            objectTemp = data.objectTemp,
                            status = data.status
                        )
                    }
                    when (val result = watchSensorRepository.insertSkinTemperatureData(entities)) {
                        is Result.Success -> {
                            totalStored += entities.size
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Stored ${entities.size} skin temperature entries locally")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to store skin temperature data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (hasAnyData && totalStored > 0) {
                    // Track when watch data is received
                    timestampService.updateLastWatchDataReceived()
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Total stored: $totalStored sensor data entries locally")
                } else {
                    Log.w(AppConfig.LogTags.PHONE_BLE, "No sensor data found in CSV")
                }
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_BLE, "Error parsing or storing sensor data: ${e.message}", e)
            }
        }
    }
}
