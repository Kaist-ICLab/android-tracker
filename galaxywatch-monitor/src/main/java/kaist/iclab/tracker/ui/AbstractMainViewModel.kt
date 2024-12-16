package kaist.iclab.tracker.ui

import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.data.WearableData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class AbstractMainViewModel() : ViewModel() {
    protected val _isRecordingState = MutableStateFlow(false)
    val isRecordingState: StateFlow<Boolean>
        get() = _isRecordingState.asStateFlow()

    protected val _dataState = MutableStateFlow(WearableData())
    val dataState: StateFlow<WearableData>
        get() = _dataState.asStateFlow()

    protected val _lapsedTime: MutableStateFlow<Long> = MutableStateFlow(0)
    val lapsedTime: StateFlow<Long>
        get() = _lapsedTime.asStateFlow()

    /* Start Recording */
    abstract fun start()

    /* Stop Recording */
    abstract fun stop()

    /* Delete All Events*/
    abstract fun delete()

    /* Export data into CSV files */
    abstract fun export()

    /* Tagging event */
    abstract fun tag()
}