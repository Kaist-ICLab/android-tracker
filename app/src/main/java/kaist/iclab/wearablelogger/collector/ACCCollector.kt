package kaist.iclab.wearablelogger.collector

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import kaist.iclab.wearablelogger.db.AccDao
import kaist.iclab.wearablelogger.db.AccEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ACCCollector(
    val androidContext: Context,
    val accDao: AccDao,
): AbstractCollector() {

    private var ACCTracker: HealthTracker? =  null
    private var healthTrackingService: HealthTrackingService? = null
    private val TAG = "ACCCollector"

    private val trackerEventListener: TrackerEventListener = object :
        TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            val listSize = list.size
            val accDataList = ArrayList<Int>() // 300 accGreenSet data points
            Log.d(TAG, "onDataReceived = timestamp: ${timestamp} ,size: ${listSize}, dataContent: ${list.getOrNull(0)}")
            for (dataPoint in list) {
                val dataTimestamp = dataPoint.timestamp
                val tmp: Array<Any> = dataPoint.b.values.toTypedArray()
                val values = IntArray(tmp.size)
                for (i in tmp.indices) {
                    values[i] = (tmp[i] as Value<Int>).value
                }
                accDataList.add(values.get(0))
                Log.d(TAG+"dataValue", "$dataTimestamp, ${values.getOrNull(0)}")
                CoroutineScope(Dispatchers.IO).launch {
                    handleRetrieval(dataTimestamp, values.get(0))
                }

            }
        }
        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
            ACCTracker!!.flush()
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
            ACCTracker = healthTrackingService?.getHealthTracker(HealthTrackerType.ACCELEROMETER)
            ACCTracker?.setEventListener(trackerEventListener)
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
//        healthTrackingService = HealthTrackingService(connectionListener, androidContext)
//        healthTrackingService?.connectService()
    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")

        try {
//            ACCTracker?.setEventListener(trackerEventListener)
            healthTrackingService = HealthTrackingService(connectionListener, androidContext)
            healthTrackingService?.connectService()
        } catch(e: Exception){
            Log.e(TAG, "ACCCollector startLogging: ${e}")
        }
    }

    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        ACCTracker?.unsetEventListener()
        healthTrackingService?.disconnectService()
    }

    private suspend fun handleRetrieval(timeStamp: Long, accData: Int) {
        accDao.insertAccEvent(
            AccEntity(timeStamp, accData)
        )
    }
}