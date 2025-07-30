package kaist.iclab.wearabletracker.state

import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorConfigStorage<C: SensorConfig>(
    initialValue: C
): StateStorage<C> {
    private val _state = MutableStateFlow(initialValue)
    override val stateFlow: StateFlow<C> = _state.asStateFlow()

    override fun get(): C {
        return stateFlow.value
    }

    override fun set(value: C) {
        _state.value = value
    }
}