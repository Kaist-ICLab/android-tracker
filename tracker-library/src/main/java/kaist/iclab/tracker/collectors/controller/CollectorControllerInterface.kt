package kaist.iclab.tracker.collectors.controller

import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.Flow

interface CollectorControllerInterface {
    val collectors: MutableList<AbstractCollector>
    fun addCollector(collector: AbstractCollector)
    fun removeCollector(collector: AbstractCollector)

    fun getCollectorsList(): List<String>
    fun isRunning():Boolean
    fun isRunningFlow(): Flow<Boolean>

    fun getCollectorConfigChange(): Flow<Map<String, Boolean>>

    fun enable(name: String, permissionManager: PermissionManagerInterface)
    fun disable(name: String)

    fun start()
    fun stop()
}