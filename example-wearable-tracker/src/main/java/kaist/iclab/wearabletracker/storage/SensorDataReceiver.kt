package kaist.iclab.wearabletracker.storage

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kaist.iclab.tracker.sensor.controller.ControllerState
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class SensorDataReceiver(
    private val context: Context,
) {
    private val serviceIntent = Intent(context, SensorDataReceiverService::class.java)
    fun startBackgroundCollection() { context.startForegroundService(serviceIntent) }
    fun stopBackgroundCollection() { context.stopService(serviceIntent) }
    class SensorDataReceiverService: Service() {
        private val sensors by inject<List<Sensor<*, *>>>(qualifier = named("sensors"))
        private val listener = sensors.associate {
            it.name to { e: SensorEntity -> Log.d(it.name, e.toString()); Unit }
        }

        override fun onBind(p0: Intent?): IBinder? = null

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val serviceNotification = BackgroundController.ControllerService.serviceNotification
            val postNotification = NotificationCompat.Builder(
                this,
                serviceNotification!!.channelId
            )
                .setSmallIcon(serviceNotification.icon)
                .setContentTitle(serviceNotification.title)
                .setContentText(serviceNotification.description)
                .setOngoing(true)
                .build()

            this.startForeground(
                BackgroundController.ControllerService.Companion.serviceNotification!!.notificationId,
                postNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )

            for (sensor in sensors) {
                sensor.addListener(listener[sensor.name]!!)
            }

            return START_STICKY
        }

        override fun onDestroy() {
            for (sensor in sensors) {
                sensor.removeListener(listener[sensor.name]!!)
            }
        }
    }
}