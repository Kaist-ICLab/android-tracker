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
    val error: String? = null
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
                _uiState.value = DataUiState(
                    isLoading = false,
                    sensors = sensors,
                    totalRecords = totalRecords
                )
            } catch (e: Exception) {
                _uiState.value = DataUiState(
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
}
