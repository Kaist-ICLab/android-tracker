package dev.iclab.tracker.ui

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainViewModelFakeImpl() : MainViewModelInterface {
    companion object{
        const val TAG = "MainViewModelFakeImpl"
    }

    override val collectorList = listOf("Battery", "Location", "Test")

    override val _isRunningState = MutableStateFlow(false)
    override val isRunningState: StateFlow<Boolean>
        get() = _isRunningState.asStateFlow()

    override val _collectorConfigState = MutableStateFlow(
        collectorList.associateWith { false }.toMap()
    )
    override val collectorConfigState: StateFlow<Map<String, Boolean>>
        get() = _collectorConfigState.asStateFlow()

    override fun start() {
        _isRunningState.value = true
    }

    override fun stop() {
        _isRunningState.value = false
    }

    override fun enable(name: String) {
        _collectorConfigState.value = _collectorConfigState.value.toMutableMap().apply {
            this[name] = true
        }
    }

    override fun disable(name: String) {
        _collectorConfigState.value = _collectorConfigState.value.toMutableMap().apply {
            this[name] = false
        }
    }

    override fun sync() {
        Log.d(TAG, "SYNC")
    }

    override fun delete() {
        Log.d(TAG, "delete")
    }
}