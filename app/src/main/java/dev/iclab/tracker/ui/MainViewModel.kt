package dev.iclab.tracker.ui

import androidx.lifecycle.ViewModel
import dev.iclab.tracker.CollectorController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class MainViewModel(
    private val collectorController: CollectorController
): ViewModel() {

    // Main UI state
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

    fun start() {
        _uiState.value = MainUiState(isRunning = true)
        collectorController.start()
    }

    fun stop() {
        _uiState.value = MainUiState(isRunning = false)
        collectorController.stop()
    }
}