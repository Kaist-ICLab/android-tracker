package kaist.iclab.wearablelogger.collector

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat

class CollectorRepository(
    val collectors: List<AbstractCollector>,
    val androidContext: Context
) {
    private val TAG = "CollectorRepository"

    init{
        collectors.forEach{
            it.setup()
        }
    }
    fun start() {
        val intent = Intent(androidContext, CollectorForegroundService::class.java)
        ContextCompat.startForegroundService(androidContext, intent)
        Log.d(TAG, "start")
    }

    fun stop() {
        val intent = Intent(androidContext, CollectorForegroundService::class.java)
        androidContext.stopService(intent)
        collectors.onEach {
            it.stopLogging()
        }
        Log.d(TAG, "stop")
    }
}