package kaist.iclab.mobiletracker.helpers

import android.content.Context
import android.util.Log
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.repository.SensorDataRepository
import kaist.iclab.mobiletracker.utils.SensorDataCsvParser
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Helper class for managing BLE communication with wearable devices.
 * Handles receiving sensor data via BLE and coordinating data upload to Supabase.
 */
class BLEHelper(
    private val context: Context,
    private val sensorDataRepository: SensorDataRepository
) {
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
            // Parse CSV and upload all sensor data to Supabase
            parseAndUploadAllSensorData(csvData)
        }
    }

    /**
     * Parse CSV data and extract all sensor data types, then upload to Supabase.
     * Uses managed coroutine scope for proper lifecycle management.
     */
    private fun parseAndUploadAllSensorData(csvData: String) {
        ioScope.launch {
            try {
                // Parse all sensor types from CSV
                val locationDataList = SensorDataCsvParser.parseLocationCsv(csvData)
                val accelerometerDataList = SensorDataCsvParser.parseAccelerometerCsv(csvData)
                val edaDataList = SensorDataCsvParser.parseEDACsv(csvData)
                val heartRateDataList = SensorDataCsvParser.parseHeartRateCsv(csvData)
                val ppgDataList = SensorDataCsvParser.parsePPGCsv(csvData)
                val skinTemperatureDataList = SensorDataCsvParser.parseSkinTemperatureCsv(csvData)
                
                // Upload each sensor type to Supabase using repository
                if (locationDataList.isNotEmpty()) {
                    when (val result = sensorDataRepository.insertLocationDataBatch(locationDataList)) {
                        is Result.Success -> {
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${locationDataList.size} location entries to Supabase")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to upload location data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (accelerometerDataList.isNotEmpty()) {
                    when (val result = sensorDataRepository.insertAccelerometerDataBatch(accelerometerDataList)) {
                        is Result.Success -> {
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${accelerometerDataList.size} accelerometer entries to Supabase")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to upload accelerometer data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (edaDataList.isNotEmpty()) {
                    when (val result = sensorDataRepository.insertEDADataBatch(edaDataList)) {
                        is Result.Success -> {
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${edaDataList.size} EDA entries to Supabase")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to upload EDA data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (heartRateDataList.isNotEmpty()) {
                    when (val result = sensorDataRepository.insertHeartRateDataBatch(heartRateDataList)) {
                        is Result.Success -> {
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${heartRateDataList.size} heart rate entries to Supabase")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to upload heart rate data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (ppgDataList.isNotEmpty()) {
                    when (val result = sensorDataRepository.insertPPGDataBatch(ppgDataList)) {
                        is Result.Success -> {
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${ppgDataList.size} PPG entries to Supabase")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to upload PPG data: ${result.message}", result.exception)
                        }
                    }
                }
                
                if (skinTemperatureDataList.isNotEmpty()) {
                    when (val result = sensorDataRepository.insertSkinTemperatureDataBatch(skinTemperatureDataList)) {
                        is Result.Success -> {
                            Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${skinTemperatureDataList.size} skin temperature entries to Supabase")
                        }
                        is Result.Error -> {
                            Log.e(AppConfig.LogTags.PHONE_BLE, "Failed to upload skin temperature data: ${result.message}", result.exception)
                        }
                    }
                }
                
                val totalEntries = locationDataList.size + accelerometerDataList.size + 
                    edaDataList.size + heartRateDataList.size + ppgDataList.size + 
                    skinTemperatureDataList.size
                
                if (totalEntries > 0) {
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Total uploaded: $totalEntries sensor data entries")
                } else {
                    Log.w(AppConfig.LogTags.PHONE_BLE, "No sensor data found in CSV")
                }
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_BLE, "Error parsing or uploading sensor data: ${e.message}", e)
            }
        }
    }
}
