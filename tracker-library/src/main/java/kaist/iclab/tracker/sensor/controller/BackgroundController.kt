package kaist.iclab.tracker.sensor.controller

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Binder
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.flow.StateFlow

class BackgroundController(
    private val context: Context,
    private val controllerStateStorage: StateStorage<ControllerState>,
    override val sensors: List<Sensor<*, *>>,
    private val serviceNotification: ServiceNotification
) : Controller {
    companion object {
        private val TAG = BackgroundController::class.simpleName
    }

    data class ServiceNotification(
        val channelId: String,
        val channelName: String,
        val notificationId: Int,
        val title: String,
        val description: String,
        val icon: Int
    )

    init {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val serviceChannel = NotificationChannel(
            serviceNotification.channelId,
            serviceNotification.channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(serviceChannel)

        sensors.forEach { it.init() }
    }
    override val controllerStateFlow: StateFlow<ControllerState> = controllerStateStorage.stateFlow


    /* Use ForegroundService to collect the data 24/7*/
    private val serviceIntent = Intent(context, ControllerService::class.java)

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun start() {
        ControllerService.stateStorage = controllerStateStorage
        ControllerService.sensors = sensors
        ControllerService.serviceNotification = serviceNotification
        context.startForegroundService(serviceIntent)
    }

    override fun stop() {
        Log.d(this::class.simpleName, "stop()")
        context.stopService(serviceIntent)
    }

    class ControllerService : Service() {
        companion object {
            private val TAG = ControllerService::class.simpleName
            var isServiceRunning = false
            var stateStorage: StateStorage<ControllerState>? = null
            var sensors: List<Sensor<*, *>>? = null
            var serviceNotification: ServiceNotification? = null
        }

        override fun onBind(intent: Intent?): Binder? = null
        override fun onDestroy() {
            Log.d(this::class.simpleName, "onDestroy()")
            stop()
            stateStorage = null
            sensors = null
            serviceNotification = null
        }

        private fun run() {
            if (sensors!!.any { it.sensorStateFlow.value.flag == SensorState.FLAG.DISABLED }) {
                Log.d(TAG, "Some sensors are disabled")
                stateStorage!!.set(
                    ControllerState(
                        ControllerState.FLAG.DISABLED,
                        "Some sensors are disabled"
                    )
                )
                throw Exception("Some sensors are disabled")
            }

            val postNotification = NotificationCompat.Builder(
                this.applicationContext,
                serviceNotification!!.channelId
            )
                .setSmallIcon(serviceNotification!!.icon)
                .setContentTitle(serviceNotification!!.title)
                .setContentText(serviceNotification!!.description)
                .setOngoing(true)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                this.startForeground(
                    serviceNotification!!.notificationId,
                    postNotification,
                    requiredForegroundServiceType()
                )
            } else {
                this.startForeground(serviceNotification!!.notificationId, postNotification)
            }

            Log.d(TAG, "Notification Post was called")
            stateStorage!!.set(ControllerState(ControllerState.FLAG.RUNNING))
            sensors!!.forEach { it.start() }
            isServiceRunning = true
        }

        private fun stop() {
            Log.d("BackgroundController", "Trying to stop...")
            Log.d("BackgroundController", "stateStorage: $stateStorage")
            isServiceRunning = false
            stateStorage!!.set(ControllerState(ControllerState.FLAG.READY))
            sensors!!.forEach { it.stop() }
            stopSelf()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            try {
                run()
            } catch (e: Exception) {
                e.printStackTrace()
                stop()
            }
            return START_STICKY
//            return super.onStartCommand(intent, flags, startId)
        }

        private fun requiredForegroundServiceType(): Int {
            val serviceTypes = sensors!!.map { sensor ->
                if (sensor.sensorStateFlow.value.flag == SensorState.FLAG.ENABLED) {
                    sensor.foregroundServiceTypes.toList()
                } else {
                    emptyList()
                }
            }.flatten().toMutableSet()
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
}