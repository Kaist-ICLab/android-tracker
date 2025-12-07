package kaist.iclab.mobiletracker.viewmodels.settings

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.repository.PhoneSensorRepository
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.services.SyncTimestampService
import kaist.iclab.mobiletracker.utils.DateTimeFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServerSyncSettingsViewModel(
    private val phoneSensorRepository: PhoneSensorRepository,
    private val context: Context
) : ViewModel() {
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
                }
                is Result.Error -> {
                    Log.e(TAG, "Error flushing sensor data: ${result.message}", result.exception)
                }
            }
            _isFlushing.value = false
        }
    }
}

