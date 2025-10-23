package kaist.iclab.wearabletracker

import kaist.iclab.tracker.sensor.controller.Controller
import kaist.iclab.tracker.sensor.controller.ControllerState

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
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.storage.DataSyncRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named
import kotlin.getValue
import kotlin.text.get

class MyBackgroundController(
    private val context: Context,
    private val controllerStateStorage: StateStorage<ControllerState>,
    override val sensors: List<Sensor<*, *>>,
    serviceNotification: ServiceNotification,
) : Controller {
    companion object {
        private val TAG = MyBackgroundController::class.simpleName
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
        context.startForegroundService(serviceIntent)
    }

    override fun stop() {
        Log.d(this::class.simpleName, "stop()")

        if (ControllerService.isServiceRunning) {
            context.stopService(serviceIntent)
        } else {
            sensors.forEach { it.stop() }
            controllerStateStorage.set(ControllerState(ControllerState.FLAG.READY))
        }
    }

    class ControllerService : Service() {
        companion object {
            private val TAG = ControllerService::class.simpleName
            var isServiceRunning = false
        }

        private val sensors by inject<List<Sensor<*, *>>>(qualifier = named("sensors"))
        private val serviceNotification by inject<ServiceNotification>()
        private val stateStorage by inject<StateStorage<ControllerState>>(qualifier = named("controllerState"))
        private val dataSyncRepository by inject<DataSyncRepository>()
        private val sensorDataStorages by inject<Map<String, BaseDao<SensorEntity, *>>>(qualifier = named("sensorDataStorages"))

        private val partialSensingAllowed = true

        private val listener: Map<String, (SensorEntity) -> Unit > = sensors.associate { it.id to
            { e: SensorEntity ->
                CoroutineScope(Dispatchers.IO).launch { sensorDataStorages[it.id]!!.insert(e) }
            }
        }

        override fun onBind(intent: Intent?): Binder? = null
        override fun onDestroy() {
            Log.d(this::class.simpleName, "onDestroy()")
            stop()
        }

        private fun run() {
            if (!(partialSensingAllowed) && sensors.any { it.sensorStateFlow.value.flag == SensorState.FLAG.DISABLED }) {
                Log.d(TAG, "Some sensors are disabled")
                stateStorage.set(
                    ControllerState(
                        ControllerState.FLAG.DISABLED,
                        "Some sensors are disabled"
                    )
                )
                throw Exception("Some sensors are disabled")
            }

            val postNotification = NotificationCompat.Builder(
                this.applicationContext,
                serviceNotification.channelId
            )
                .setSmallIcon(serviceNotification.icon)
                .setContentTitle(serviceNotification.title)
                .setContentText(serviceNotification.description)
                .setOngoing(true)
                .build()

            dataSyncRepository.startSending()

            this.startForeground(
                serviceNotification.notificationId,
                postNotification,
                requiredForegroundServiceType()
            )

            Log.d(TAG, "Notification Post was called")
            stateStorage.set(ControllerState(ControllerState.FLAG.RUNNING))

            // Setup sensors
            sensors.filter { it.sensorStateFlow.value.flag == SensorState.FLAG.ENABLED }
                .forEach { it.start() }

            for (sensor in sensors) {
                sensor.addListener(listener[sensor.id]!!)
            }

            isServiceRunning = true
        }

        private fun stop() {
            Log.d(TAG, "Trying to stop...")
            Log.d(TAG, "stateStorage: $stateStorage")
            isServiceRunning = false
            stateStorage.set(ControllerState(ControllerState.FLAG.READY))

            for (sensor in sensors) {
                sensor.removeListener(listener[sensor.id]!!)
            }

            sensors.filter { it.sensorStateFlow.value.flag == SensorState.FLAG.RUNNING }
                .forEach { it.stop() }

            dataSyncRepository.stopSending()

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
        }

        private fun requiredForegroundServiceType(): Int {
            val defaultServiceType =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            return sensors.filter {
                it.sensorStateFlow.value.flag in listOf(
                    SensorState.FLAG.ENABLED,
                    SensorState.FLAG.RUNNING
                )
            }
                .map { it.foregroundServiceTypes.toList() }
                .flatten()
                .toSet()
                .fold(defaultServiceType, { acc, type -> acc or type })
        }
    }
}