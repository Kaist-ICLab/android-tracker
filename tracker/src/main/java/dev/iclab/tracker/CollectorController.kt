package dev.iclab.tracker

import android.content.Context
import android.content.Intent
import android.os.Build
import dev.iclab.tracker.collectors.AbstractCollector
import dev.iclab.tracker.collectors.TestCollector
import dev.iclab.tracker.database.DatabaseInterface

class CollectorController(
    private val context: Context,
    private val database: DatabaseInterface
){
    companion object {
        const val TAG = "CollectorController"
    }
    private val serviceIntent = Intent(context, CollectorService::class.java)
    internal val collectors = mutableListOf<AbstractCollector>()

    fun addCollector(collector:AbstractCollector) {
        collectors.add(collector)
    }
    fun removeCollector(collector:AbstractCollector) {
        collectors.remove(collector)
    }
    fun getCollectorsList(): List<String> {
        return collectors.map { it.NAME }
    }

    fun isRunning():Boolean {
        return false
    }

    fun enable(name: String, permissionManager: PermissionManager) {
        collectors.forEach {
            if(it.NAME == name) {
                it.enable(permissionManager){ enabled->
                    if(enabled) { database.update("CONFIG", mapOf(it.NAME to true)) }
                }
            }
        }
    }

    fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        }else {
            context.startService(serviceIntent)
        }
    }
    fun stop() {
        context.stopService(serviceIntent)
    }
}