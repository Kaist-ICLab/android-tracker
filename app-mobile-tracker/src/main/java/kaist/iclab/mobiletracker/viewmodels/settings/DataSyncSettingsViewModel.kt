package kaist.iclab.mobiletracker.viewmodels.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.repository.PhoneSensorRepository
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.utils.AppToast
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kaist.iclab.tracker.sensor.core.Sensor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.qualifier.named

class DataSyncSettingsViewModel(
    private val phoneSensorRepository: PhoneSensorRepository,
    private val context: Context
) : ViewModel(), KoinComponent {
    private val sensors: List<Sensor<*, *>> by inject(qualifier = named("sensors"))
    private val TAG = "ServerSyncSettingsViewModel"
    private val timestampService = SyncTimestampService(context)

    // Current time (updates every second)
    private val _currentTime = MutableStateFlow(DateTimeFormatter.getCurrentTimeFormatted())
    val currentTime: StateFlow<String> = _currentTime.asStateFlow()

    // Last watch data received
    private val _lastWatchData = MutableStateFlow<String?>(null)
    val lastWatchData: StateFlow<String?> = _lastWatchData.asStateFlow()

    // Last phone sensor data
    private val _lastPhoneSensor = MutableStateFlow<String?>(null)
    val lastPhoneSensor: StateFlow<String?> = _lastPhoneSensor.asStateFlow()

    // Last successful upload
    private val _lastSuccessfulUpload = MutableStateFlow<String?>(null)
    val lastSuccessfulUpload: StateFlow<String?> = _lastSuccessfulUpload.asStateFlow()

    // Next scheduled upload
    private val _nextScheduledUpload = MutableStateFlow<String?>(null)
    val nextScheduledUpload: StateFlow<String?> = _nextScheduledUpload.asStateFlow()

    // Data collection started
    private val _dataCollectionStarted = MutableStateFlow<String?>(null)
    val dataCollectionStarted: StateFlow<String?> = _dataCollectionStarted.asStateFlow()

    // Flush operation state
    private val _isFlushing = MutableStateFlow(false)
    val isFlushing: StateFlow<Boolean> = _isFlushing.asStateFlow()

    // Per-sensor deletion state - maps sensor ID to deletion state
    private val _deletingSensors = MutableStateFlow<Set<String>>(emptySet())
    val deletingSensors: StateFlow<Set<String>> = _deletingSensors.asStateFlow()

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
        _lastPhoneSensor.value = timestampService.getLastPhoneSensorData()
        _lastSuccessfulUpload.value = timestampService.getLastSuccessfulUpload()
        _nextScheduledUpload.value = timestampService.getNextScheduledUpload()
        _dataCollectionStarted.value = timestampService.getDataCollectionStarted()
    }

    /**
     * Flush all local sensor data
     */
    fun flushAllData() {
        viewModelScope.launch {
            _isFlushing.value = true
            when (val result = phoneSensorRepository.flushAllData()) {
                is Result.Success -> {
                    // Data flushed successfully
                    AppToast.show(context, R.string.toast_data_deleted)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error flushing sensor data: ${result.message}", result.exception)
                }
            }
            _isFlushing.value = false
        }
    }

    /**
     * Delete data for a specific sensor
     * @param sensorId The ID of the sensor to delete data for
     */
    fun deleteSensorData(sensorId: String) {
        viewModelScope.launch {
            _deletingSensors.value = _deletingSensors.value + sensorId
            when (val result = phoneSensorRepository.deleteAllSensorData(sensorId)) {
                is Result.Success -> {
                    // Data deleted successfully
                    AppToast.show(context, R.string.toast_sensor_data_deleted)
                }
                is Result.Error -> {
                    Log.e(TAG, "Error deleting sensor data for $sensorId: ${result.message}", result.exception)
                }
            }
            _deletingSensors.value = _deletingSensors.value - sensorId
        }
    }

    /**
     * Check if a specific sensor is currently being deleted
     */
    fun isDeletingSensor(sensorId: String): Boolean {
        return _deletingSensors.value.contains(sensorId)
    }

    /**
     * Get sensor ID from sensor name
     * @param sensorName The name of the sensor
     * @return The sensor ID, or null if not found
     */
    fun getSensorId(sensorName: String): String? {
        return sensors.firstOrNull { it.name == sensorName }?.id
    }

    /**
     * Check if a sensor has storage available
     * @param sensorId The sensor ID to check
     * @return true if storage is available, false otherwise
     */
    fun hasStorageForSensor(sensorId: String): Boolean {
        return phoneSensorRepository.hasStorageForSensor(sensorId)
    }
}

