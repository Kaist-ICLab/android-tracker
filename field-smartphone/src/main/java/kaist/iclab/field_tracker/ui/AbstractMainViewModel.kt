package kaist.iclab.field_tracker.ui

import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.collectors.AbstractCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractMainViewModel: ViewModel() {
    abstract val _isRunningState: MutableStateFlow<Boolean>
    val isRunningState: StateFlow<Boolean>
        get() = _isRunningState.asStateFlow()

    abstract val collectorMap: Map<String, AbstractCollector>
    abstract val _enabledCollectors: MutableStateFlow<Map<String, Boolean>>
    val enabledCollectors: StateFlow<Map<String, Boolean>>
        get() = _enabledCollectors.asStateFlow()

    abstract fun start()
    abstract fun stop()
    abstract fun enable(name: String)
    abstract fun disable(name: String)

    abstract fun sync()
    abstract fun delete()
}