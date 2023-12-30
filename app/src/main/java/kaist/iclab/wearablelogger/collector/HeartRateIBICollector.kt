package kaist.iclab.wearablelogger.collector

import android.content.Context
import android.hardware.SensorManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import kaist.iclab.wearablelogger.db.HRIBIDao
import kaist.iclab.wearablelogger.db.HRIBIEntity
import kaist.iclab.wearablelogger.db.PpgEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HeartRateIBICollector(
    val androidContext: Context,
    val hribiDao: HRIBIDao,
): AbstractCollector() {

    private var heartRateIBITracker: HealthTracker? =  null
    private var healthTrackingService: HealthTrackingService? = null
    private val TAG = "HeartRateIBICollector"

    private val trackerEventListener: HealthTracker.TrackerEventListener = object :
        HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            val listSize = list.size
            Log.d(TAG, "onDataReceived = timestamp: $timestamp ,size: ${listSize}, dataContent: ${list.getOrNull(0)}")
            for (dataPoint in list) {
                val dataTimestamp = dataPoint.timestamp
                val tmp: Array<Any> = dataPoint.b.values.toTypedArray()
                val fVal = (tmp[0] as Value<Any>).value
                Log.d(TAG+"dataValue", "$dataTimestamp, ${fVal}")
                CoroutineScope(Dispatchers.IO).launch {
                    handleRetrieval(dataTimestamp, fVal.toString())
                }

            }

        }
        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
            heartRateIBITracker!!.flush()
        }
        override fun onError(trackerError: HealthTracker.TrackerError) {
            if (trackerError == HealthTracker.TrackerError.PERMISSION_ERROR) {
                Log.d(TAG, "onError = Permission Failed")
            } else if (trackerError == HealthTracker.TrackerError.SDK_POLICY_ERROR) {
                Log.d(TAG, "onError = SDK policy denied")
            } else {
                Log.d(TAG, "onError = Unknown Error ${trackerError}")
            }
        }
    }
    private val connectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            heartRateIBITracker = healthTrackingService?.getHealthTracker(HealthTrackerType.HEART_RATE)
            heartRateIBITracker?.setEventListener(trackerEventListener)
            Log.d(TAG, "connectionListener onConnectionSuccess")
        }
        override fun onConnectionEnded() {
            Log.d(TAG, "connectionListener onConnectionEnded")
        }
        override fun onConnectionFailed(e: HealthTrackerException) {
            Log.d(TAG, "connectionListener onConnectionFailed: ${e}")
        }
    }
    override fun setup() {
        Log.d(TAG, "setup()")

    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")

        try {

            healthTrackingService = HealthTrackingService(connectionListener, androidContext)
            healthTrackingService?.connectService()
        } catch(e: Exception){
            Log.e(TAG, "HeartRateIBICollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        heartRateIBITracker?.unsetEventListener()
        healthTrackingService?.disconnectService()
        heartRateIBITracker!!.flush()
    }
    private suspend fun handleRetrieval(timeStamp: Long, hribiData: String) {
        hribiDao.insertHRIBIEvent(
            HRIBIEntity(timeStamp, hribiData)
        )
    }
}