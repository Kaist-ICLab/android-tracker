package kaist.iclab.tracker.collectors.controller

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectorController(
    private val context: Context
) : CollectorControllerInterface {
    companion object {
        const val TAG = "CollectorController"
        const val CONFIG_COLLECTION = "CONFIG"
        const val RUNNING_COLLECTION = "RUNNING"
    }

    private val database = Tracker.getDatabase()

    private val serviceIntent = Intent(context, CollectorService::class.java)
    override val collectors = mutableListOf<AbstractCollector>()

    override fun addCollector(collector: AbstractCollector) {
        collectors.add(collector)
        if(!(collector.NAME in database.getLastDoc(CONFIG_COLLECTION))) {
            database.update(
                CONFIG_COLLECTION, database.getLastDoc(CONFIG_COLLECTION)
                    + mapOf(collector.NAME to false, "timestamp" to System.currentTimeMillis()))
        }
    }

    override fun removeCollector(collector: AbstractCollector) {
        collectors.remove(collector)
        database.update(CONFIG_COLLECTION, database.getLastDoc(CONFIG_COLLECTION) - collector.NAME + mapOf("timestamp" to System.currentTimeMillis()))
    }

    override fun getCollectorsList(): List<String> {
        return collectors.map { it.NAME }
    }

    override fun isRunning(): Boolean {
        val map = database.getLastDoc(RUNNING_COLLECTION)
        return (map["running"] as Boolean?) ?: false
    }

    override fun getCollectorConfigChange(): Flow<Map<String, Boolean>> {
        return database.getLastDocFlow(CONFIG_COLLECTION) as Flow<Map<String, Boolean>>
    }

    override fun isRunningFlow(): Flow<Boolean> {
        return database.getLastDocFlow(RUNNING_COLLECTION).map {
            (it["running"] as Boolean?) ?: false
        }
    }

    override fun enable(name: String, permissionManager: PermissionManagerInterface) {
        collectors.forEach {
            if (it.NAME == name) {
                Log.d(TAG, "Enabling $name")
                it.enable(permissionManager) { enabled ->
                    if (enabled) {
                        database.update(
                            CONFIG_COLLECTION,
                            database.getLastDoc(CONFIG_COLLECTION)
                                    + mapOf(it.NAME to true, "timestamp" to System.currentTimeMillis()))
                    }
                }
            }
        }
    }

    override fun disable(name: String) {
        collectors.forEach {
            if (it.NAME == name) {
                database.update(
                    CONFIG_COLLECTION,
                    database.getLastDoc(CONFIG_COLLECTION)
                            + mapOf(it.NAME to false, "timestamp" to System.currentTimeMillis()))
            }
        }
    }

    override fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    override fun stop() {
        context.stopService(serviceIntent)
    }
}