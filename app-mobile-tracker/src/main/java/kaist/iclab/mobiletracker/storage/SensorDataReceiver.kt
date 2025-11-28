package kaist.iclab.mobiletracker.storage

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kaist.iclab.mobiletracker.db.dao.BaseDao
import kaist.iclab.tracker.sensor.controller.BackgroundController
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
        private val sensorDataStorages by inject<Map<String, BaseDao<SensorEntity>>>(qualifier = named("sensorDataStorages"))
        private val serviceNotification by inject<BackgroundController.ServiceNotification>()

        // Uncomment the logs if you want to verify the data is received
        private val listener: Map<String, (SensorEntity) -> Unit > = sensors.associate { it.id to
            { e: SensorEntity ->
                // Log.d("SensorDataReceiver", "[PHONE] - Data received from ${it.name}: $e")
                sensorDataStorages[it.id]?.let { dao ->
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            dao.insert(e)
                            // Log.d("SensorDataReceiver", "[PHONE] - Successfully stored data from ${it.name}")
                        } catch (ex: Exception) {
                            Log.e("SensorDataReceiver", "[PHONE] - Failed to store data from ${it.name}: ${ex.message}", ex)
                        }
                    }
                } ?: Log.w("SensorDataReceiver", "[PHONE] - No DAO found for sensor ${it.name} (${it.id})")
            }
        }

        override fun onBind(p0: Intent?): IBinder? = null

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val postNotification = NotificationCompat.Builder(
                this,
                serviceNotification.channelId
            )
                .setSmallIcon(serviceNotification.icon)
                .setContentTitle(serviceNotification.title)
                .setContentText(serviceNotification.description)
                .setOngoing(true)
                .build()

            val serviceType = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE else 0

            this.startForeground(
                serviceNotification.notificationId,
                postNotification,
                serviceType
            )

            // Remove listeners first to prevent duplicates if onStartCommand is called multiple times
            for (sensor in sensors) {
                sensor.removeListener(listener[sensor.id]!!)
            }
            
            // Then add listeners
            for (sensor in sensors) {
                sensor.addListener(listener[sensor.id]!!)
            }

            return START_STICKY
        }

        override fun onDestroy() {
            for (sensor in sensors) {
                sensor.removeListener(listener[sensor.id]!!)
            }
        }
    }
}
