package kaist.iclab.wearabletracker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.tracker.sync.internet.InternetDataChannel
import kaist.iclab.tracker.sync.internet.InternetMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlin.getValue

class DataProxyService: Service() {
    companion object {
        private val TAG = DataProxyService::class.java.simpleName
        const val NOTIFICATION_CHANNEL_ID = "DataProxyServiceChannel"
        const val NOTIFICATION_ID = 278590245 // Use a unique ID for your notification
    }

    private val bleDataChannel by lazy { BLEDataChannel(this) }
    private val internetDataChannel by lazy { InternetDataChannel() }

    private val nameMap = mapOf(
        "Accelerometer" to "acc",
        "PPG" to "ppg",
        "SkinTemperature" to "skintemp",
        "EDA" to "eda",
        "HeartRate" to "hr"
    )
    private val listener = { key: String, jsonElement: JsonElement ->
        jsonElement.jsonObject.toMap().forEach { (key, value) ->
            if(key in nameMap) {
                val name = nameMap[key]!!
                CoroutineScope(Dispatchers.IO).launch {
                    internetDataChannel.send("http://logging.iclab.dev/${name}_summary", value.toString(), InternetMethod.POST)
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "DataProxyService created.")

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Data Proxy Service",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Data Proxy Service")
            .setContentText("Sending data to server...")
            .setOngoing(true)
            .build()

        val foregroundServiceType =
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) ServiceInfo.FOREGROUND_SERVICE_TYPE_REMOTE_MESSAGING
            else ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE

        startForeground(
            NOTIFICATION_ID,
            notification,
            foregroundServiceType
        )

        Log.d(TAG, "DataProxyService is running.")

        // Register data channel listener to receive data from a wearable
        bleDataChannel.addOnReceivedListener(setOf("data"), listener)

        // If the service is killed, it will be automatically restarted.
        // Be cautious: Koin-injected dependencies will be null if the app process was killed.
        // For a truly robust service, consider passing dependencies via the intent
        // or checking for null and calling stopSelf().
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        // Unregister listeners to prevent memory leaks
        bleDataChannel.removeOnReceivedListener(setOf("data"), listener)
        Log.d(TAG, "DataProxyService destroyed.")
    }
}
