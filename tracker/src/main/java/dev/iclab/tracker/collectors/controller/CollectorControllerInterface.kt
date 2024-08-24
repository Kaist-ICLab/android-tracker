package dev.iclab.tracker.collectors.controller

import dev.iclab.tracker.collectors.AbstractCollector
import dev.iclab.tracker.permission.PermissionManager
import kotlinx.coroutines.flow.Flow

interface CollectorControllerInterface {
    val collectors: MutableList<AbstractCollector>
    fun addCollector(collector:AbstractCollector)
    fun removeCollector(collector:AbstractCollector)

    fun getCollectorsList(): List<String>
    fun isRunning():Boolean
    fun isRunningFlow(): Flow<Boolean>

    fun getCollectorConfigChange(): Flow<Map<String, Boolean>>

    fun enable(name: String, permissionManager: PermissionManager)
    fun disable(name: String)

    fun start()
    fun stop()
}