package kaist.iclab.tracker.controller

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kaist.iclab.tracker.Tracker


class CollectorService(): Service() {
    companion object {
        const val TAG = "CollectorService"
        const val CHANNEL_ID = "COLLECTOR_WORKING"
        const val CHANNEL_NUMBER = 1
        const val NOTF_TITLE = "Collector Working..."
        const val NOTF_DESCRIPTION = "Collector is tracking for your daily life!"

        /* Notification Channel Register
        * Required once after created
        * */
        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(CHANNEL_ID, NOTF_TITLE, importance).apply {
                    description = NOTF_DESCRIPTION
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand")
        run()
        val notification: Notification =
            NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle(NOTF_TITLE)
                .setContentText(NOTF_DESCRIPTION)
                .setOngoing(true)
                .build()
        ServiceCompat.startForeground(this, CHANNEL_NUMBER, notification,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                /*TODO: Change dynamically based on enabled collectors*/
            } else {
                0
            }
        )

        return super.onStartCommand(intent, flags, startId)
    }
    override fun onDestroy() {
        super.onDestroy()
        Tracker.getDatabase().log("SERVICE_STOPPED")
        Tracker.getCollectorController().collectors.forEach {
            it.stop()
        }
        Tracker.getDatabase().update(CollectorController.RUNNING_COLLECTION, mapOf("running" to false, "timestamp" to System.currentTimeMillis()))
    }

    fun run() {
        Log.d(TAG, "run")
        Tracker.getDatabase().log("SERVICE_STARTED")
        Tracker.getCollectorController().collectors.forEach {
            it.start()
        }
        Tracker.getDatabase().update(CollectorController.RUNNING_COLLECTION, mapOf("running" to true, "timestamp" to System.currentTimeMillis()))
    }
}