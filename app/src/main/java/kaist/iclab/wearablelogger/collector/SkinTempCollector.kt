package kaist.iclab.wearablelogger.collector

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import kaist.iclab.wearablelogger.HealthTrackerRepo
import kaist.iclab.wearablelogger.db.SkinTempDao
import kaist.iclab.wearablelogger.db.SkinTempEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkinTempCollector(
    val androidContext: Context,
    val healthTrackerRepo: HealthTrackerRepo,
    val skinTempDao: SkinTempDao,
): AbstractCollector() {

    private var SkinTempTracker: HealthTracker? =  null
    private val TAG = javaClass.simpleName

    private val trackerEventListener: HealthTracker.TrackerEventListener = object :
        HealthTracker.TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            val timestamp = System.currentTimeMillis()
            val listSize = list.size
            val skinTempDataList = ArrayList<Int>()
            Log.d(TAG, "onDataReceived = timestamp: ${timestamp} ,size: ${list.size}")
            for (dataPoint in list) {
                val dataTimestamp = dataPoint.timestamp
                val tmp: Array<Any> = dataPoint.b.values.toTypedArray()
                val values = IntArray(tmp.size)
                for (i in tmp.indices) {
                    values[i] = (tmp[i] as Value<Int>).value
                }
                skinTempDataList.add(values.get(0))
                Log.d(TAG+"dataValue", "$dataTimestamp, ${values.getOrNull(0)}")
                CoroutineScope(Dispatchers.IO).launch {
                    handleRetrieval(dataTimestamp, values.get(0))
                }

            }
        }
        override fun onFlushCompleted() {
            Log.d(TAG, "onFlushCompleted")
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
    override fun setup() {
        Log.d(TAG, "setup()")
    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")
        try {
            SkinTempTracker = healthTrackerRepo.healthTrackingService.getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS)
            SkinTempTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "SkinTempCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        SkinTempTracker?.unsetEventListener()
    }

    private suspend fun handleRetrieval(timeStamp: Long, skinTempData: Int) {
        skinTempDao.insertSkinTempEvent(
            SkinTempEntity(timeStamp, skinTempData)
        )
    }
}