package kaist.iclab.tracker.collectors

import android.Manifest
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.wifi.WifiManager
import android.os.Build
import kaist.iclab.tracker.triggers.SystemBroadcastTrigger
import java.util.concurrent.TimeUnit

class WiFiScanCollector(
    override val context: Context
) : AbstractCollector(context) {

//    No Configuration!

    data class DataEntity(
        val timestamp: Long,
        val bssid: String,
        val frequency: Int,
        val level: Int
    ) : AbstractCollector.DataEntity()

    override val permissions = listOfNotNull(
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()


    override fun isAvailable(): Boolean {
        return wifiManager.isWifiEnabled
    }

    private val wifiManager: WifiManager by lazy {
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val broadcastTrigger = SystemBroadcastTrigger(
        context,
        arrayOf(
            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
        )
    ) {
        val results = wifiManager.scanResults
        val timestamp = System.currentTimeMillis()
        results.forEach {
            listener?.invoke(
                DataEntity(
                    timestamp,
                    it.BSSID,
                    it.frequency,
                    it.level,
                )
            )
        }
    }

    override fun start() {
        broadcastTrigger.register()
    }

    override fun stop() {
        broadcastTrigger.unregister()
    }
}