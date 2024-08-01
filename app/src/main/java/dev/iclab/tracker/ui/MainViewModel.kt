package dev.iclab.tracker.ui

import androidx.lifecycle.ViewModel
import dev.iclab.tracker.CollectorController
import dev.iclab.tracker.database.DatabaseInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Thread.sleep


class MainViewModel(
    private val collectorController: CollectorController,
    private val databaseInterface: DatabaseInterface
): ViewModel() {

    // Main UI state
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun start() {
        _uiState.value = MainUiState(isRunning = true)
        collectorController.start()

//        CoroutineScope(Dispatchers.IO).launch {
//            while(true) {
//                _uiState.value = _uiState.value.copy(
//                    data = databaseInterface.queryAllDocs("test")
//                )
//                sleep(1000)
//            }
//        }

    }

    fun stop() {
        _uiState.value = MainUiState(isRunning = false)
        collectorController.stop()
    }
}