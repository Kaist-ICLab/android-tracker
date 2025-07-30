package kaist.iclab.wearabletracker.state

import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ControllerStateStorage: StateStorage<ControllerState> {
    private val _stateFlow = MutableStateFlow(ControllerState(ControllerState.FLAG.DISABLED, ""))
    override val stateFlow: StateFlow<ControllerState>
        get() = _stateFlow.asStateFlow()

    override fun get(): ControllerState {
        return stateFlow.value
    }

    override fun set(value: ControllerState) {
        _stateFlow.value = value
    }

}