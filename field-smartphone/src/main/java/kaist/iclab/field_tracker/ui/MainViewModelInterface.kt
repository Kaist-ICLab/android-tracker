package kaist.iclab.field_tracker.ui

import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.CollectorInterface
import kaist.iclab.tracker.controller.CollectorState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MainViewModelInterface {
    val collectors: Array<String>
    val controllerStateFlow: StateFlow<Boolean>

    val collectorStateFlow: Flow<Map<String, CollectorState>>
    val configFlow: Flow<Map<String, CollectorConfig>>

    fun start()
    fun stop()
    fun enableCollector(name: String)
    fun disableCollector(name: String)

    fun getDeviceInfo(): String
    fun getAppVersion(): String


//    fun sync()
//    fun delete()
}