package kaist.iclab.tracker.sensor.controller

import kaist.iclab.tracker.sensor.core.Sensor
import kotlinx.coroutines.flow.StateFlow

/**
 * The Controller interface manages a list of sensors, tracks the controller's state,
 * and provides methods to start and stop its operation.
 */
interface Controller {
    /**
     * A list of sensors managed by the controller.
     */
    val sensors: List<Sensor<*, *>>

    /**
     * A StateFlow representing the current state of the controller.
     */
    val controllerStateFlow: StateFlow<ControllerState>

    /**
     * Starts the controller operation.
     */
    fun start()

    /**
     * Stops the controller operation.
     */
    fun stop()
}
