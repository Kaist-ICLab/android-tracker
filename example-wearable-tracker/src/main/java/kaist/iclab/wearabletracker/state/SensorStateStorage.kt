package kaist.iclab.wearabletracker.state

import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SensorStateStorage: StateStorage<SensorState> {
    private val _state = MutableStateFlow(SensorState(SensorState.FLAG.UNAVAILABLE, ""))
    override val stateFlow: StateFlow<SensorState> = _state.asStateFlow()

    override fun get(): SensorState {
        return stateFlow.value
    }

    override fun set(value: SensorState) {
        _state.value = value
    }
}