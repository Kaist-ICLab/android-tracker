package kaist.iclab.tracker.controller

import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

interface CollectorControllerInterface {
    val collectors: MutableList<AbstractCollector>
    val _stateFlow: MutableSharedFlow<Boolean>

    fun add(collector: AbstractCollector)
    fun remove(collector: AbstractCollector)

    fun start()
    fun stop()
    fun isRunningFlow(): Flow<Boolean>
}