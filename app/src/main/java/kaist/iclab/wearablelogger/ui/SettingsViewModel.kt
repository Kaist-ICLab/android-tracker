package kaist.iclab.wearablelogger.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.wearablelogger.collector.CollectorRepository
import kaist.iclab.wearablelogger.config.ConfigRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val configRepository: ConfigRepository,
    private val collectorRepository: CollectorRepository
): ViewModel() {
    private val TAG = javaClass.simpleName

    val uiState: StateFlow<SettingUiState> =
        configRepository.sensorStatusFlow.map{
            SettingUiState(it)
        }.stateIn(
            scope = CoroutineScope(Dispatchers.IO),
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = SettingUiState(listOf())
        )
    val isCollectorState: StateFlow<Boolean> =
        configRepository.isCollectingFlow
            .stateIn(
                scope = CoroutineScope(Dispatchers.IO),
                started = SharingStarted.WhileSubscribed(5_000L),
                initialValue = false
            )

    fun update(sensorName: String, status: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            configRepository.updateSensorStatus(sensorName, status)
        }
    }

    fun startLogging(){
        collectorRepository.start()
        CoroutineScope(Dispatchers.IO).launch{
            configRepository.updateCollectorStatus(true)
        }
    }
    fun stopLogging(){
        collectorRepository.stop()
        CoroutineScope(Dispatchers.IO).launch{
            configRepository.updateCollectorStatus(false)
        }
    }

    fun upload(){
        Log.d(TAG, "UPLOAD")
        collectorRepository.upload()
    }

    fun flush(){
        collectorRepository.flush()
    }

}

data class SettingUiState(
    val sensorStates: List<SensorState>
)

data class SensorState(
    val name: String,
    val isEnabled: Boolean,
)
