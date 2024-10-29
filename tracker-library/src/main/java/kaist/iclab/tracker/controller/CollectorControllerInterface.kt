package kaist.iclab.tracker.controller

import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.Flow

interface CollectorControllerInterface {

    fun add(collector: AbstractCollector)
    fun remove(collector: AbstractCollector)

    fun start()
    fun stop()
    fun isRunningFlow(): Flow<Boolean>
}