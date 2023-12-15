package kaist.iclab.wearablelogger.collector


import android.content.Context
import android.util.Log
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.collect.Iterables
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerError
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import kaist.iclab.wearablelogger.db.TestDao
import kaist.iclab.wearablelogger.db.TestEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PPGGreenCollector(
    val androidContext: Context,
    val testDao: TestDao
): AbstractCollector() {

    private var ppgGreenTracker: HealthTracker? =  null
    private var healthTrackingService: HealthTrackingService? = null
    private val TAG = "PPGGreenCollector"

    private val trackerEventListener: TrackerEventListener = object : TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            // TODO
            val listSize = list.size
            val ppgDataList = ArrayList<Int>()
            Log.d(TAG, "onDataReceived = timestamp: ${timestamp} ,size: ${listSize}, dataContent: ${list.getOrNull(0)}")
            for (dataPoint in list) {
                val dataTimestamp = dataPoint.timestamp
                val tmp: Array<Any> = dataPoint.b.values.toTypedArray()
                val values = IntArray(tmp.size)
                for (i in tmp.indices) {
//                    values[i] = (tmp[i] as Value<Int?>).value ?: 0
                    values[i] = (tmp[i] as Value<Int>).value
                }
                ppgDataList.add(values.get(0))
                Log.d(TAG+"dataValue", "$dataTimestamp, ${values.getOrNull(0)}")
            }
            Log.d(TAG+"dataValues", "${ppgDataList.average()}")
            val ppgDataAvg = ppgDataList.average()
//            CoroutineScope(Dispatchers.IO).launch {
//                testDao.nukeTable()
//                testDao.insertTestEvent(
//                    TestEntity(
//                        timestamp = System.currentTimeMillis(),
//                        dummy = ppgDataAvg,
//                        )
//                )
//                testDao.queryTestEvent(0L).collect{
//                    Log.d("PPGGreenCollector", it.toString())
//                }
//            }
        }
        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
        }
        override fun onError(trackerError: TrackerError) {
            if (trackerError == TrackerError.PERMISSION_ERROR) {
                Log.d(TAG, "onError = Permission Failed")
            } else if (trackerError == TrackerError.SDK_POLICY_ERROR) {
                Log.d(TAG, "onError = SDK policy denied")
            } else {
                Log.d(TAG, "onError = Unknown Error ${trackerError}")
            }
        }
    }
    private val connectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            ppgGreenTracker = healthTrackingService?.getHealthTracker(HealthTrackerType.PPG_GREEN)
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
        healthTrackingService = HealthTrackingService(connectionListener, androidContext)
        healthTrackingService?.connectService()
    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")

        try {
            ppgGreenTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "PPGGreenCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        ppgGreenTracker?.unsetEventListener()
        healthTrackingService?.disconnectService()
    }
}