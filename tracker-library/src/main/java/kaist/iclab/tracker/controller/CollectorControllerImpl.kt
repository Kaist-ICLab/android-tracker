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
import kaist.iclab.tracker.collectors.AbstractCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class CollectorControllerImpl(
    private val context: Context
) : CollectorControllerInterface {
    private val serviceIntent = Intent(context, CollectorService::class.java)
    override val collectors = mutableListOf<AbstractCollector>()

    override val _stateFlow = MutableSharedFlow<Boolean>(replay = 1)

    override fun add(collector: AbstractCollector) {
        collectors.add(collector)
    }

    override fun remove(collector: AbstractCollector) {
        collectors.remove(collector)
    }

    override fun isRunningFlow(): Flow<Boolean> = _stateFlow.asSharedFlow()


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

    class CollectorService: Service() {
        val controller = Tracker.getCollectorController()
        val collectors = controller.collectors
        override fun onBind(intent: Intent?): IBinder? = null
        override fun onDestroy() {
            stop()
        }

        fun run() {
            ServiceCompat.startForeground(
                this,
                NotificationHandler.CHANNEL_NUMBER,
                NotificationHandler.createNotification(this),
                requiredForegroundServiceType()
            )
            Log.d("CollectorService", "Service starts running")
            Log.d("CollectorService", controller._stateFlow.tryEmit(true).toString())
            collectors.forEach { collector ->
                collector.start()
            }
        }

        fun stop() {
            controller._stateFlow.tryEmit(false)
            collectors.forEach { collector ->
                collector.stop()
            }
            stopSelf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            try {
                run()
            } catch (e: Exception) {
                Log.e("CollectorService", "ERROR:${e}")
                stop()
            }
            return super.onStartCommand(intent, flags, startId)
        }

        fun requiredForegroundServiceType(): Int {
            val serviceTypes = mutableSetOf<Int>()
            collectors.forEach {
                serviceTypes.addAll(it.foregroundServiceTypes)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                serviceTypes.add(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            }
            return if (serviceTypes.isNotEmpty()) {
                serviceTypes.reduce { acc, type -> acc or type }
            } else {
                0
            }
        }
    }

    object NotificationHandler {
        const val CHANNEL_ID = "COLLECTOR_WORKING"
        const val CHANNEL_NUMBER = 1
        const val NOTF_TITLE = "Collector Working..."
        const val NOTF_DESCRIPTION = "Collector is tracking for your daily life!"

        fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    NOTF_TITLE,
                    importance
                ).apply {
                    description = NOTF_DESCRIPTION
                }
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        fun createNotification(context: Context): Notification {
            return NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle(NOTF_TITLE)
                .setContentText(NOTF_DESCRIPTION)
                .setOngoing(true)
                .build()
        }
    }
}