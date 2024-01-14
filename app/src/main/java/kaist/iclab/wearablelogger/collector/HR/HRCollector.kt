package kaist.iclab.wearablelogger.collector.HR

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.healthtracker.AbstractTrackerEventListener
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HRCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val hrDao: HRDao,
) : AbstractCollector() {
    private var hrTracker: HealthTracker? = null
    override val TAG = javaClass.simpleName

    private val trackerEventListener: HealthTracker.TrackerEventListener = object :
        AbstractTrackerEventListener() {
        override fun onDataReceived(data: List<DataPoint>) {
            val dataReceived = System.currentTimeMillis()
            Log.d(TAG, "$dataReceived, ${data.size}")
            val hrEntities = data.map {
                HREntity(
                    dataReceived = dataReceived,
                    timestamp = it.timestamp,
                    hr = it.getValue(ValueKey.HeartRateSet.HEART_RATE),
                    hrStatus = it.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS),
                    ibi = it.getValue(ValueKey.HeartRateSet.IBI_LIST),
                    ibiStatus = it.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST),
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                hrDao.insertHREvents(hrEntities)
            }
        }
    }

    override fun startLogging() {
        Log.d(TAG, "startLogging")
        try {
            hrTracker = healthTrackerRepo.healthTrackingService.getHealthTracker(
                HealthTrackerType.HEART_RATE
            )
            hrTracker?.setEventListener(trackerEventListener)
        } catch (e: Exception) {
            Log.e(TAG, "HeartRateIBICollector startLogging: ${e}")
        }
    }

    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        hrTracker?.unsetEventListener()
    }
}