package dev.iclab.tracker

import android.content.Context
import android.content.Intent
import android.os.Build
import dev.iclab.tracker.collectors.AbstractCollector
import dev.iclab.tracker.collectors.TestCollector

class CollectorController(
    private val context: Context
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