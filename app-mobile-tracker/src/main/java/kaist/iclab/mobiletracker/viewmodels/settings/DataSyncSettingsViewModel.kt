package kaist.iclab.mobiletracker.viewmodels.settings

import android.content.Context
import android.content.res.Configuration
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.repository.PhoneSensorRepository
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.repository.WatchSensorRepository
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.services.upload.PhoneSensorUploadService
import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService
import kaist.iclab.mobiletracker.utils.AppToast
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kaist.iclab.mobiletracker.utils.SensorTypeHelper
import kaist.iclab.tracker.sensor.core.Sensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named
import java.text.NumberFormat
import java.util.Locale

/**
 * Data class representing sensor data information
 */
data class SensorDataInfo(
    val latestTimestamp: Long?,
    val recordCount: Int,
    val lastSyncTimestamp: Long?
)

class DataSyncSettingsViewModel(
    private val phoneSensorRepository: PhoneSensorRepository,
    private val watchSensorRepository: WatchSensorRepository,
    private val timestampService: SyncTimestampService,
    private val context: Context
) : ViewModel(), KoinComponent {
    private val sensors: List<Sensor<*, *>> by inject(qualifier = named("sensors"))
    private val phoneSensorUploadService: PhoneSensorUploadService by inject()
    private val watchSensorUploadService: WatchSensorUploadService by inject()
    private val TAG = "ServerSyncSettingsViewModel"

    // Current time (updates every second)
    private val _currentTime = MutableStateFlow(DateTimeFormatter.getCurrentTimeFormatted())
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    // Last watch data received
    private val _lastWatchData = MutableStateFlow<String?>(null)
    val lastWatchData: StateFlow<String?> = _lastWatchData.asStateFlow()

    // Last successful upload
    private val _lastSuccessfulUpload = MutableStateFlow<String?>(null)
    val lastSuccessfulUpload: StateFlow<String?> = _lastSuccessfulUpload.asStateFlow()

    // Flush operation state
    private val _isFlushing = MutableStateFlow(false)
    val isFlushing: StateFlow<Boolean> = _isFlushing.asStateFlow()

    // Per-sensor deletion state - maps sensor ID to deletion state
    private val _deletingSensors = MutableStateFlow<Set<String>>(emptySet())
    val deletingSensors: StateFlow<Set<String>> = _deletingSensors.asStateFlow()

    // Per-sensor upload state - maps sensor ID to upload state
    private val _uploadingSensors = MutableStateFlow<Set<String>>(emptySet())
    val uploadingSensors: StateFlow<Set<String>> = _uploadingSensors.asStateFlow()

    // Per-sensor data info - maps sensor ID to SensorDataInfo
    private val _sensorDataInfo = MutableStateFlow<Map<String, SensorDataInfo>>(emptyMap())
    val sensorDataInfo: StateFlow<Map<String, SensorDataInfo>> = _sensorDataInfo.asStateFlow()

    init {
        // Update current time every second
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000)
                _currentTime.value = DateTimeFormatter.getCurrentTimeFormatted()
            }
        }

        // Load timestamps from service
        loadTimestamps()

        // Load sensor data info (latest timestamp and record count)
        loadSensorDataInfo()

        // Refresh timestamps periodically (every 5 seconds)
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(5000)
                loadTimestamps()
            }
        }
    }

    /**
     * Load all timestamps from SyncTimestampService
     */
    private fun loadTimestamps() {
        _lastWatchData.value = timestampService.getLastWatchDataReceived()
        _lastSuccessfulUpload.value = timestampService.getLastSuccessfulUpload()
    }

    /**
     * Load sensor data info (latest timestamp, record count, and last sync timestamp) for all sensors
     * Includes both phone sensors and watch sensors
     */
    private fun loadSensorDataInfo() {
        viewModelScope.launch {
            val infoMap = mutableMapOf<String, SensorDataInfo>()

            // Load phone sensor data info
            sensors.forEach { sensor ->
                val sensorId = sensor.id
                if (phoneSensorRepository.hasStorageForSensor(sensorId)) {
                    try {
                        val latestTimestamp =
                            phoneSensorRepository.getLatestRecordedTimestamp(sensorId)
                        val recordCount = phoneSensorRepository.getRecordCount(sensorId)
                        val lastSyncTimestamp =
                            timestampService.getLastSuccessfulUploadTimestamp(sensorId)
                        infoMap[sensorId] = SensorDataInfo(
                            latestTimestamp = latestTimestamp,
                            recordCount = recordCount,
                            lastSyncTimestamp = lastSyncTimestamp
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading sensor data info for $sensorId: ${e.message}", e)
                    }
                }
            }

            // Load watch sensor data info
            SensorTypeHelper.watchSensorIds.forEach { sensorId ->
                try {
                    val latestTimestamp = watchSensorRepository.getLatestTimestamp(sensorId)
                    val recordCount = watchSensorRepository.getRecordCount(sensorId)
                    val lastSyncTimestamp =
                        timestampService.getLastSuccessfulUploadTimestamp(sensorId)
                    infoMap[sensorId] = SensorDataInfo(
                        latestTimestamp = latestTimestamp,
                        recordCount = recordCount,
                        lastSyncTimestamp = lastSyncTimestamp
                    )
                } catch (e: Exception) {
                    Log.e(
                        TAG,
                        "Error loading watch sensor data info for $sensorId: ${e.message}",
                        e
                    )
                }
            }

            _sensorDataInfo.value = infoMap
        }
    }

    /**
     * Refresh sensor data info for a specific sensor
     * Supports both phone sensors and watch sensors
     */
    private fun refreshSensorDataInfo(sensorId: String) {
        viewModelScope.launch {
            try {
                val latestTimestamp: Long?
                val recordCount: Int
                val lastSyncTimestamp = timestampService.getLastSuccessfulUploadTimestamp(sensorId)

                // Check if it's a watch sensor
                if (SensorTypeHelper.isWatchSensor(sensorId)) {
                    // Watch sensor
                    latestTimestamp = watchSensorRepository.getLatestTimestamp(sensorId)
                    recordCount = watchSensorRepository.getRecordCount(sensorId)
                } else if (phoneSensorRepository.hasStorageForSensor(sensorId)) {
                    // Phone sensor
                    latestTimestamp = phoneSensorRepository.getLatestRecordedTimestamp(sensorId)
                    recordCount = phoneSensorRepository.getRecordCount(sensorId)
                } else {
                    return@launch
                }

                val newInfo = SensorDataInfo(
                    latestTimestamp = latestTimestamp,
                    recordCount = recordCount,
                    lastSyncTimestamp = lastSyncTimestamp
                )
                _sensorDataInfo.value = _sensorDataInfo.value + (sensorId to newInfo)
            } catch (e: Exception) {
                Log.e(TAG, "Error refreshing sensor data info for $sensorId: ${e.message}", e)
            }
        }
    }

    /**
     * Format number with commas (e.g., 1234 -> "1,234")
     */
    fun formatRecordCount(count: Int): String {
        return NumberFormat.getNumberInstance(Locale.getDefault()).format(count)
    }

    /**
     * Format timestamp to string
     */
    fun formatTimestamp(timestamp: Long?): String? {
        return timestamp?.let { DateTimeFormatter.formatTimestampShort(it) }
    }

    /**
     * Flush all local sensor data
     */
    fun flushAllData() {
        viewModelScope.launch {
            _isFlushing.value = true

            // Delete all phone sensor data
            val phoneResult = phoneSensorRepository.flushAllData()

            // Delete all watch sensor data
            val watchResult = watchSensorRepository.flushAllData()

            // Check if both operations succeeded
            when {
                phoneResult is Result.Success && watchResult is Result.Success -> {
                    // Both deletions succeeded
                    // Clear all sync-related timestamps (except next scheduled upload)
                    timestampService.clearAllSyncTimestamps()
                    AppToast.show(context, R.string.toast_data_deleted)
                    // Refresh all sensor data info
                    loadSensorDataInfo()
                }

                phoneResult is Result.Error -> {
                    Log.e(
                        TAG,
                        "Error flushing phone sensor data: ${phoneResult.message}",
                        phoneResult.exception
                    )
                    // Still try to clear timestamps even if deletion partially failed
                    timestampService.clearAllSyncTimestamps()
                }

                watchResult is Result.Error -> {
                    Log.e(
                        TAG,
                        "Error flushing watch sensor data: ${watchResult.message}",
                        watchResult.exception
                    )
                    // Still try to clear timestamps even if deletion partially failed
                    timestampService.clearAllSyncTimestamps()
                }
            }

            _isFlushing.value = false
        }
    }

    /**
     * Delete data for a specific sensor
     * Supports both phone sensors and watch sensors
     * @param sensorId The ID of the sensor to delete data for
     */
    fun deleteSensorData(sensorId: String) {
        viewModelScope.launch {
            _deletingSensors.value = _deletingSensors.value + sensorId

            val result = if (SensorTypeHelper.isWatchSensor(sensorId)) {
                // Watch sensor
                watchSensorRepository.deleteAllSensorData(sensorId)
            } else {
                // Phone sensor
                phoneSensorRepository.deleteAllSensorData(sensorId)
            }

            when (result) {
                is Result.Success -> {
                    // Data deleted successfully
                    // Clear the sync timestamp for this sensor
                    timestampService.clearLastSuccessfulUpload(sensorId)
                    AppToast.show(context, R.string.toast_sensor_data_deleted)
                    // Refresh sensor data info
                    refreshSensorDataInfo(sensorId)
                }

                is Result.Error -> {
                    Log.e(
                        TAG,
                        "Error deleting sensor data for $sensorId: ${result.message}",
                        result.exception
                    )
                }
            }
            _deletingSensors.value = _deletingSensors.value - sensorId
        }
    }

    /**
     * Get sensor ID from string resource ID
     * This method uses the English string resource to match against sensor names,
     * avoiding localization issues
     * Supports both phone sensors (from sensors list) and watch sensors (from WatchSensorUploadService constants)
     * @param sensorNameRes The string resource ID for the sensor name
     * @return The sensor ID, or null if not found
     */
    fun getSensorIdFromResource(sensorNameRes: Int): String? {
        // Get the English string by creating a configuration with English locale
        val config = Configuration(context.resources.configuration)
        config.setLocale(Locale.ENGLISH)
        val englishResources = context.createConfigurationContext(config).resources
        val englishName = englishResources.getString(sensorNameRes)

        // First, try to find in phone sensors
        val phoneSensorId = sensors.firstOrNull { it.name == englishName }?.id
        if (phoneSensorId != null) {
            return phoneSensorId
        }

        // If not found, check if it's a watch sensor by matching against watch sensor IDs
        return SensorTypeHelper.watchSensorIds.firstOrNull { it == englishName }
    }

    /**
     * Check if a sensor has storage available
     * @param sensorId The sensor ID to check
     * @return true if storage is available, false otherwise
     */
    fun hasStorageForSensor(sensorId: String): Boolean {
        return phoneSensorRepository.hasStorageForSensor(sensorId)
    }

    /**
     * Check if a specific sensor is currently being uploaded
     */
    fun isUploadingSensor(sensorId: String): Boolean {
        return _uploadingSensors.value.contains(sensorId)
    }

    /**
     * Upload sensor data to Supabase
     * @param sensorId The ID of the sensor to upload data for
     * Supports both phone sensors and watch sensors
     */
    fun uploadSensorData(sensorId: String) {
        viewModelScope.launch {
            _uploadingSensors.value = _uploadingSensors.value + sensorId

            try {
                // Check if it's a phone sensor first (since LocationSensor can be both phone and watch)
                val sensor = sensors.firstOrNull { it.id == sensorId }
                if (sensor != null) {
                    // Upload phone sensor data
                    // Check if data is available
                    if (!phoneSensorUploadService.hasDataToUpload(sensorId, sensor)) {
                        AppToast.show(context, R.string.toast_no_data_to_upload)
                        _uploadingSensors.value = _uploadingSensors.value - sensorId
                        return@launch
                    }

                    // Upload data using the upload service
                    when (val result =
                        phoneSensorUploadService.uploadSensorData(sensorId, sensor)) {
                        is Result.Success -> {
                            AppToast.show(context, R.string.toast_sensor_data_uploaded)
                            // Update per-sensor last successful upload timestamp
                            timestampService.updateLastSuccessfulUpload(sensorId)
                            loadTimestamps()
                            // Refresh sensor data info (in case data was deleted after upload)
                            refreshSensorDataInfo(sensorId)
                        }

                        is Result.Error -> {
                            Log.e(
                                TAG,
                                "Error uploading sensor data for $sensorId: ${result.message}",
                                result.exception
                            )
                            if (result.exception is UnsupportedOperationException) {
                                AppToast.show(context, R.string.toast_upload_not_implemented)
                            } else {
                                AppToast.show(context, R.string.toast_sensor_data_upload_error)
                            }
                        }
                    }
                } else if (SensorTypeHelper.isWatchSensor(sensorId)) {
                    // Upload watch sensor data (only if not found in phone sensors)
                    if (!watchSensorUploadService.hasDataToUpload(sensorId)) {
                        AppToast.show(context, R.string.toast_no_data_to_upload)
                        _uploadingSensors.value = _uploadingSensors.value - sensorId
                        return@launch
                    }

                    when (val result = watchSensorUploadService.uploadSensorData(sensorId)) {
                        is Result.Success -> {
                            AppToast.show(context, R.string.toast_sensor_data_uploaded)
                            timestampService.updateLastSuccessfulUpload(sensorId)
                            loadTimestamps()
                            refreshSensorDataInfo(sensorId)
                        }
                        is Result.Error -> {
                            Log.e(TAG, "Error uploading watch sensor data for $sensorId: ${result.message}", result.exception)
                            AppToast.show(context, R.string.toast_sensor_data_upload_error)
                        }
                    }

                } else {
                    Log.w(TAG, "Sensor not found: $sensorId")
                    AppToast.show(context, R.string.toast_upload_not_implemented)
                    _uploadingSensors.value = _uploadingSensors.value - sensorId
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading sensor data: ${e.message}", e)
                AppToast.show(context, R.string.toast_sensor_data_upload_error)
            } finally {
                _uploadingSensors.value = _uploadingSensors.value - sensorId
            }
        }
    }

    /**
     * Upload all sensor data (both phone and watch) to Supabase.
     * Uses per-sensor upload logic (including SyncTimestampService) to avoid re-uploading
     * previously sent data, so this operation is idempotent across runs.
     */
    fun uploadAllSensorData() {
        viewModelScope.launch {
            try {
                var uploadedCount = 0
                var skippedCount = 0
                var failedCount = 0
                
                // Upload all phone sensors
                sensors.forEach { sensor ->
                    val sensorId = sensor.id
                    _uploadingSensors.value = _uploadingSensors.value + sensorId
                    
                    try {
                        // Check if data is available (but don't show toast for individual sensors)
                        if (!phoneSensorUploadService.hasDataToUpload(sensorId, sensor)) {
                            skippedCount++
                        } else {
                            // Upload data using the upload service
                            when (val result =
                                phoneSensorUploadService.uploadSensorData(sensorId, sensor)) {
                                is Result.Success -> {
                                    uploadedCount++
                                    // Update per-sensor last successful upload timestamp
                                    timestampService.updateLastSuccessfulUpload(sensorId)
                                }
                                is Result.Error -> {
                                    failedCount++
                                    Log.e(
                                        TAG,
                                        "Error uploading sensor data for ${sensor.name}: ${result.message}",
                                        result.exception
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        failedCount++
                        Log.e(TAG, "Exception uploading sensor data for ${sensor.name}: ${e.message}", e)
                    } finally {
                        _uploadingSensors.value = _uploadingSensors.value - sensorId
                    }
                }
                
                // Upload all watch sensors
                SensorTypeHelper.watchSensorIds.forEach { sensorId ->
                    _uploadingSensors.value = _uploadingSensors.value + sensorId
                    
                    try {
                        if (!watchSensorUploadService.hasDataToUpload(sensorId)) {
                            skippedCount++
                        } else {
                            when (val result = watchSensorUploadService.uploadSensorData(sensorId)) {
                                is Result.Success -> {
                                    uploadedCount++
                                    timestampService.updateLastSuccessfulUpload(sensorId)
                                }
                                is Result.Error -> {
                                    failedCount++
                                    Log.e(TAG, "Error uploading watch sensor data for $sensorId: ${result.message}", result.exception)
                                }
                            }
                        }
                    } catch (e: Exception) {
                        failedCount++
                        Log.e(TAG, "Exception uploading watch sensor data for $sensorId: ${e.message}", e)
                    } finally {
                        _uploadingSensors.value = _uploadingSensors.value - sensorId
                    }
                }
                
                // Show summary toast
                val totalSensorsCount = sensors.size + SensorTypeHelper.watchSensorIds.size
                when {
                    uploadedCount > 0 -> {
                        // Show how many sensors successfully uploaded
                        AppToast.show(
                            context,
                            context.getString(R.string.toast_upload_all_summary, uploadedCount)
                        )
                    }
                    skippedCount == totalSensorsCount -> {
                        // All sensors skipped (no new data)
                        AppToast.show(context, R.string.toast_no_data_to_upload)
                    }
                    failedCount > 0 && uploadedCount == 0 -> {
                        // All attempts failed and none were successful
                        AppToast.show(context, R.string.toast_sensor_data_upload_error)
                    }
                }
                
                // Refresh sensor data info and timestamps
                loadTimestamps()
                loadSensorDataInfo()
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading all sensor data: ${e.message}", e)
                AppToast.show(context, R.string.toast_sensor_data_upload_error)
            }
        }
    }
}

