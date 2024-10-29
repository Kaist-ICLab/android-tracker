package kaist.iclab.tracker.controller

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.ServiceCompat
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.controller.CollectorService.Companion
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class CollectorController(
    private val context: Context
) : CollectorControllerInterface {


    private val serviceIntent = Intent(context, CollectorService::class.java)
    private val collectors = mutableListOf<AbstractCollector>()

    override fun add(collector: AbstractCollector) {
        collectors.add(collector)
    }

    override fun remove(collector: AbstractCollector) {
        collectors.remove(collector)
    }

    private val _isRunningFlow = Flow<Boolean>()
    override fun isRunningFlow(): Flow<Boolean> {

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

    object CollectorService : Service() {
        override fun onBind(intent: Intent?): IBinder? = null
        override fun onDestroy() {
        }



        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            ServiceCompat.startForeground(this, 1, notification, requiredForegroundServiceType())

        }

        fun requiredForegroundServiceType(): Int {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            } else {
                0
            }
        }
    }
    object CollectorServiceNotificationHandler {
        const val CHANNEL_ID = "COLLECTOR_WORKING"
        const val CHANNEL_NUMBER = 1
        const val NOTF_TITLE = "Collector Working..."
        const val NOTF_DESCRIPTION = "Collector is tracking for your daily life!"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(kaist.iclab.tracker.controller.CollectorService.CHANNEL_ID, kaist.iclab.tracker.controller.CollectorService.NOTF_TITLE, importance).apply {
                    description = kaist.iclab.tracker.controller.CollectorService.NOTF_DESCRIPTION
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }
}