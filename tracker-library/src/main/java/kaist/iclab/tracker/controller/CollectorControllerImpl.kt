package kaist.iclab.tracker.controller

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.AbstractCollector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class CollectorControllerImpl(
    private val context: Context
) : CollectorControllerInterface {
    private val serviceIntent = Intent(context, CollectorService::class.java)
    override val collectors = mutableListOf<AbstractCollector>()
    private val _stateFlow = MutableSharedFlow<Boolean>(replay = 1)

    override fun add(collector: AbstractCollector) {
        collectors.add(collector)
    }

    override fun remove(collector: AbstractCollector) {
        collectors.remove(collector)
    }

    override fun isRunningFlow(): Flow<Boolean> = _stateFlow.asSharedFlow()
    override fun updateState(isRunning: Boolean) {
        _stateFlow.tryEmit(isRunning)
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

    class CollectorService: Service() {
        val controller = Tracker.getCollectorController()
        val notfManager = Tracker.getNotfManager()
        val collectors = controller.collectors
        override fun onBind(intent: Intent?): IBinder? = null
        override fun onDestroy() {
            stop()
        }

        fun run() {
            notfManager.startForegroundService(
                this,
                requiredForegroundServiceType()
            )
            controller.updateState(true)
            collectors.forEach { collector ->
                collector.start()
            }
        }

        fun stop() {
            controller.updateState(false)
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

//    object NotificationHandler {
//        val SERVICE_CHANNEL_ID = "TRACKER_SERVICE"
//        val SERVICE_CHANNEL_NUMBER = 1
//
//        val NOTF_TITLE = "Tracker Service"
//        val NOTF_DESCRIPTION = "Tracker is running now"
//
//        fun createServiceNotfChannel(context: Context) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                val channel = NotificationChannel(
//                    SERVICE_CHANNEL_ID,
//                    NOTF_TITLE,
//                    NotificationManager.IMPORTANCE_DEFAULT
//                ).apply {
//                    description = NOTF_DESCRIPTION
//                }
//                // Register the channel with the system
//                val notificationManager: NotificationManager =
//                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//                notificationManager.createNotificationChannel(channel)
//            }
//        }
//
//        fun createServiceNotf(service: Service): Notification {
//            Log.d("NOTIFICATION_SERVICE", "Creating Notification")
//            val builder =  NotificationCompat.Builder(service, SERVICE_CHANNEL_ID)
//            return builder
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle(NOTF_TITLE)
//                .setContentText(NOTF_DESCRIPTION)
//                .setOngoing(true)
//                .build()
//        }
//    }
}