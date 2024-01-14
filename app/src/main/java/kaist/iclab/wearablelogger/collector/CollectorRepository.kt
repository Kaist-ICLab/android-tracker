package kaist.iclab.wearablelogger.collector

import android.content.Context
import android.content.Intent
import android.util.Log

class CollectorRepository(
    val collectors: List<AbstractCollector>,
    val androidContext: Context
) {
    private val TAG = "CollectorRepository"

    init {
        collectors.forEach {
            it.setup()
        }
    }

    fun start(sensorStates: List<Boolean>) {
        val intent = Intent(androidContext, CollectorService::class.java)
        collectors[1].startLogging()
//        intent.putExtra("sensorStates", sensorStates.toBooleanArray())
//        ContextCompat.startForegroundService(androidContext, intent)
        Log.d(TAG, "start")
    }

    fun stop(sensorStates: List<Boolean>) {
//        val intent = Intent(androidContext, CollectorService::class.java)
//        androidContext.stopService(intent)
//        collectors.zip(sensorStates.toList()) { collector, enabled ->
//            if (enabled) {
//                collector.stopLogging()
//            }
//        }
        Log.d(TAG, "stop")
    }

    fun flush(){
        collectors.forEach {
            it.flush()
        }
    }
}