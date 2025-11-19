package kaist.iclab.mobiletracker.helpers

import android.content.Context
import android.util.Log
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService
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
    private val locationSensorService: LocationSensorService,
    private val accelerometerSensorService: AccelerometerSensorService,
    private val edaSensorService: EDASensorService,
    private val heartRateSensorService: HeartRateSensorService,
    private val ppgSensorService: PPGSensorService,
    private val skinTemperatureSensorService: SkinTemperatureSensorService
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
                
                // Upload each sensor type to Supabase (now using suspend functions)
                if (locationDataList.isNotEmpty()) {
                    locationSensorService.insertLocationSensorDataBatch(locationDataList)
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${locationDataList.size} location entries to Supabase")
                }
                
                if (accelerometerDataList.isNotEmpty()) {
                    accelerometerSensorService.insertAccelerometerSensorDataBatch(accelerometerDataList)
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${accelerometerDataList.size} accelerometer entries to Supabase")
                }
                
                if (edaDataList.isNotEmpty()) {
                    edaSensorService.insertEDASensorDataBatch(edaDataList)
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${edaDataList.size} EDA entries to Supabase")
                }
                
                if (heartRateDataList.isNotEmpty()) {
                    heartRateSensorService.insertHeartRateSensorDataBatch(heartRateDataList)
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${heartRateDataList.size} heart rate entries to Supabase")
                }
                
                if (ppgDataList.isNotEmpty()) {
                    ppgSensorService.insertPPGSensorDataBatch(ppgDataList)
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${ppgDataList.size} PPG entries to Supabase")
                }
                
                if (skinTemperatureDataList.isNotEmpty()) {
                    skinTemperatureSensorService.insertSkinTemperatureSensorDataBatch(skinTemperatureDataList)
                    Log.d(AppConfig.LogTags.PHONE_BLE, "Uploaded ${skinTemperatureDataList.size} skin temperature entries to Supabase")
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
