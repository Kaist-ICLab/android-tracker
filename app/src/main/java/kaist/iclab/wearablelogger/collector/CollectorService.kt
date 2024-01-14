package kaist.iclab.wearablelogger.collector

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import org.koin.android.ext.android.inject

class CollectorService : Service() {

    private val collectorRepository by inject<CollectorRepository>()
    private val TAG = javaClass.simpleName
    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()
        collectorRepository.collectors.forEach {
            it.startLogging()
        }
//        if (intent != null && intent.hasExtra("sensorStates")) {
//            val sensorStates = intent.getBooleanArrayExtra("sensorStates")?.toList() ?: emptyList()
//            collectorRepository.collectors.zip(sensorStates.toList()) { collector, enabled ->
//                if (enabled) {
//                    collector.startLogging()
//                }
//            }
//        }

        val notification: Notification =
            NotificationCompat.Builder(this, "CONTINUE_LOGGING")
                .setContentTitle("WearableLogger")
                .setContentText("Running...")
                .build()
        Log.d(TAG, "onStartCommand")
        startForeground(1, notification)
        return START_STICKY
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            "CONTINUE_LOGGING",
            "CONTINUE_LOGGING",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        manager.createNotificationChannel(channel)
    }
}