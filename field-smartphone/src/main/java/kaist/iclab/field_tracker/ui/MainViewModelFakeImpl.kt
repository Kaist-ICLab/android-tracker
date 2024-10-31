package kaist.iclab.field_tracker.ui

import android.util.Log
import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.collectors.BatteryCollector
import kotlinx.coroutines.flow.MutableStateFlow

class  MainViewModelFakeImpl(

) : AbstractMainViewModel() {
    companion object{
        const val TAG = "MainViewModelFakeImpl"
    }

    override val collectorMap: Map<String, AbstractCollector> = mapOf()
    override val _enabledCollectors: MutableStateFlow<Map<String, Boolean>>
        = MutableStateFlow(
            mapOf("Battery" to false)
        )

    override val _isRunningState = MutableStateFlow(false)


    override fun start() {
        _isRunningState.value = true
    }

    override fun stop() {
        _isRunningState.value = false
    }

    override fun enable(name: String) {
        _enabledCollectors.value = _enabledCollectors.value.toMutableMap().apply {
            this[name] = true
        }

    }

    override fun disable(name: String) {
        _enabledCollectors.value = _enabledCollectors.value.toMutableMap().apply {
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