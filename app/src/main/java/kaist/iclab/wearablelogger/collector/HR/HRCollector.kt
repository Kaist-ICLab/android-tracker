package kaist.iclab.wearablelogger.collector.HR

import android.util.Log
import com.google.gson.Gson
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.wearablelogger.ToggleStates
import kaist.iclab.wearablelogger.collector.ACC.AccEntity
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.healthtracker.AbstractTrackerEventListener
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HRCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val hrDao: HRDao,
    val toggleStates: ToggleStates
) : AbstractCollector {
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

    override fun setup() {}
    override fun startLogging() {
        if (!toggleStates.hrState) {
            return
        }
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
        if (!toggleStates.hrState) {
            return
        }
        Log.d(TAG, "stopLogging")
        hrTracker?.unsetEventListener()
    }
    override fun zip2prepareSend(): ArrayList<String> {
        val gson = Gson()
        val savedDataList: List<HREntity> = hrDao.getAll()
        Log.d(TAG, "savedHRDataList: ${savedDataList.toString()}")
        val jsonList = ArrayList<String>()
        savedDataList.forEach { hrEntity ->
            val jsonStr = gson.toJson(hrEntity)
            jsonList.add(jsonStr)
        }
        return jsonList
    }
    override fun flush() {
        Log.d(TAG, "Flush HR Data")
        CoroutineScope(Dispatchers.IO).launch {
            hrDao.deleteAll()
            Log.d(TAG, "deleteAll() for HR Data")
        }
    }
}