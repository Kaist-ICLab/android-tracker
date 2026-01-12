package kaist.iclab.mobiletracker.viewmodels.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.repository.DataRepository
import kaist.iclab.mobiletracker.repository.SensorInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * UI state for the Data screen.
 */
data class DataUiState(
    val isLoading: Boolean = true,
    val sensors: List<SensorInfo> = emptyList(),
    val totalRecords: Int = 0,
    val error: String? = null,
    val isUploading: Boolean = false,
    val isDeleting: Boolean = false
)

/**
 * ViewModel for the Data screen.
 * Provides sensor list with record counts and last recorded times.
 */
class DataViewModel(
    private val dataRepository: DataRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DataUiState())
    val uiState: StateFlow<DataUiState> = _uiState.asStateFlow()

    init {
        loadSensorInfo()
    }

    /**
     * Load sensor information from the repository.
     */
    fun loadSensorInfo() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val sensors = dataRepository.getAllSensorInfo()
                val totalRecords = sensors.sumOf { it.recordCount }
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    sensors = sensors,
                    totalRecords = totalRecords
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load sensor data"
                )
            }
        }
    }

    /**
     * Refresh the sensor list.
     */
    fun refresh() {
        loadSensorInfo()
    }

    /**
     * Upload all data for all sensors.
     */
    fun uploadAllData() {
        if (_uiState.value.isUploading) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploading = true)
            try {
                val successCount = dataRepository.uploadAllData()
                // You could show a toast here if you had context, but typically 
                // we handle UI effects in the screen.
                // For simplicity, we just refresh.
                loadSensorInfo()
            } catch (e: Exception) {
                // Error handling
            } finally {
                _uiState.value = _uiState.value.copy(isUploading = false)
            }
        }
    }

    /**
     * Delete all data for all sensors.
     */
    fun deleteAllData() {
        if (_uiState.value.isDeleting) return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeleting = true)
            try {
                dataRepository.deleteAllAllData()
                loadSensorInfo()
            } catch (e: Exception) {
                // Error handling
            } finally {
                _uiState.value = _uiState.value.copy(isDeleting = false)
            }
        }
    }
}
