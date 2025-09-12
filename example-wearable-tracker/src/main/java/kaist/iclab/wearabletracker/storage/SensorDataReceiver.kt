package kaist.iclab.wearabletracker.storage

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
import kaist.iclab.tracker.sensor.core.Sensor
import kaist.iclab.tracker.sensor.core.SensorEntity
import org.koin.android.ext.android.inject
import org.koin.core.qualifier.named

class SensorDataReceiver(
    private val context: Context,
) {
    private val serviceIntent = Intent(context, SensorDataReceiverService::class.java)
    fun startBackgroundCollection() { context.startForegroundService(serviceIntent) }
    
    fun stopBackgroundCollection() { context.stopService(serviceIntent) }
    class SensorDataReceiverService: Service() {
        companion object {
            private const val TAG = "SensorDataReceiverService"
            private const val NOTIFICATION_ID = 2001
            private const val CHANNEL_ID = "sensor_data_receiver_channel"
        }
        
        private val sensors by inject<List<Sensor<*, *>>>(qualifier = named("sensors"))
        private val listener = sensors.associate {
            it.name to { e: SensorEntity -> Log.d(it.name, e.toString()); Unit }
        }

        override fun onBind(p0: Intent?): IBinder? = null

        override fun onCreate() {
            super.onCreate()
            createDefaultNotificationChannel()
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            val postNotification = NotificationCompat.Builder(
                this,
                CHANNEL_ID
            )
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Wearable Tracker - Sensor Data Collection")
                .setContentText("Collecting sensor data from wearable device")
                .setOngoing(true)
                .build()

            this.startForeground(
                NOTIFICATION_ID,
                postNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
            )

            for (sensor in sensors) {
                sensor.addListener(listener[sensor.name]!!)
            }

            return START_STICKY
        }
        
        private fun createDefaultNotificationChannel() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    CHANNEL_ID,
                    "Wearable Tracker",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Collects sensor data from wearable device"
                    setShowBadge(false)
                }
                
                val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }

        override fun onDestroy() {
            for (sensor in sensors) {
                sensor.removeListener(listener[sensor.name]!!)
            }
        }
    }
}