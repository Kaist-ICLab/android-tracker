package kaist.iclab.tracker.controller

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
//import kaist.iclab.tracker.notification.NotfManager
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.flow.StateFlow

class BackgroundController(
    private val context: Context,
    private val controllerStateStorage: StateStorage<ControllerState>,
    override val sensors: List<Sensor<*, *>>
) : Controller {
    init { sensors.forEach { it.init() } }

    override val controllerStateFlow: StateFlow<ControllerState> = controllerStateStorage.stateFlow

    /* Use ForegroundService to run always*/
    private val serviceIntent = Intent(context, ControllerService::class.java)
    override fun start() {
        context.startForegroundService(serviceIntent)
    }

    override fun stop() {
        context.stopService(serviceIntent)
    }

    class ControllerService : Service() {
        private val controller: BackgroundController = TODO()
//        private val notfManager: NotfManager = TODO()
        private val stateStorage = controller.controllerStateStorage
        private val sensors = controller.sensors

        override fun onBind(intent: Intent?): IBinder? = null
        override fun onDestroy() {
            stop()
        }

        private fun run() {
            if(sensors.any { it.sensorStateFlow.value.flag == SensorState.FLAG.DISABLED }){
                stateStorage.set(ControllerState(ControllerState.FLAG.DISABLED, "Some sensors are disabled"))
            }else{
//                notfManager.postForegroundService(
//                    this,
//                    requiredForegroundServiceType()
//                )
                Log.d("CollectorService", "Notification Post was called")
                stateStorage.set(ControllerState(ControllerState.FLAG.RUNNING))
                sensors.forEach { it.start() }
            }
        }

        private fun stop() {
            stateStorage.set(ControllerState(ControllerState.FLAG.READY))
            sensors.forEach { it.stop() }
            stopSelf()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            try {
                run()
            } catch (e: Exception) {
                stop()
            }
            return super.onStartCommand(intent, flags, startId)
        }

        private fun requiredForegroundServiceType(): Int {
            val serviceTypes = sensors.map { sensor ->
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