package kaist.iclab.tracker.controller

import kaist.iclab.tracker.sensor.core.Sensor
import kotlinx.coroutines.flow.StateFlow

interface Controller {
    val sensors: List<Sensor<*, *>>
    val controllerStateFlow: StateFlow<ControllerState>
    fun start()
    fun stop()
}