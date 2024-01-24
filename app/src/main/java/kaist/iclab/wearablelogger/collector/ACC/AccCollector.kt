package kaist.iclab.wearablelogger.collector.ACC

import android.util.Log
import com.google.gson.Gson
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.wearablelogger.ToggleStates
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.collector.PPGGreen.PpgEntity
import kaist.iclab.wearablelogger.healthtracker.AbstractTrackerEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AccCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val accDao: AccDao,
    val toggleStates: ToggleStates
) : AbstractCollector {

    private var AccTracker: HealthTracker? = null
    override val TAG = javaClass.simpleName

    private val trackerEventListener: TrackerEventListener = object :
        AbstractTrackerEventListener() {
            override fun onDataReceived(data: List<DataPoint>) {
                val dataReceived = System.currentTimeMillis()
                Log.d(TAG, "$dataReceived, ${data.size}")
                val accEntities = data.map {
                    AccEntity(
                        dataReceived = dataReceived,
                        timestamp = it.timestamp,
                        x = convert2SIUnit(it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_X)),
                        y = convert2SIUnit(it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Y)),
                        z = convert2SIUnit(it.getValue(ValueKey.AccelerometerSet.ACCELEROMETER_Z))
                    )
                }
                CoroutineScope(Dispatchers.IO).launch {
                    accDao.insertAccEvents(accEntities)
                }
            }

            private fun convert2SIUnit(value: Int): Float {
                return (9.81f / (16383.75f / 4.0f)) * value.toFloat()
            }
        }

    override fun setup() {}
    override fun startLogging() {
        if (!toggleStates.accState) {
            return
        }
        Log.d(TAG, "startLogging")
        try {
            AccTracker =
                healthTrackerRepo.healthTrackingService.getHealthTracker(HealthTrackerType.ACCELEROMETER)
            AccTracker?.setEventListener(trackerEventListener)
        } catch (e: Exception) {
            Log.e(TAG, "ACCCollector startLogging: ${e}")
        }
    }

    override fun stopLogging() {
        if (!toggleStates.accState) {
            return
        }
        Log.d(TAG, "stopLogging")
        AccTracker?.unsetEventListener()
    }
    override fun zip2prepareSend(): ArrayList<String> {
        val gson = Gson()
        val savedDataList: List<AccEntity> = accDao.getAll()
        Log.d(TAG, "savedAccDataList: ${savedDataList.toString()}")
        val jsonList = ArrayList<String>()
        savedDataList.forEach { accEntity ->
            val jsonStr = gson.toJson(accEntity)
            jsonList.add(jsonStr)
        }
        return jsonList
    }

    override fun flush() {
        Log.d(TAG, "Flush ACC Data")
        CoroutineScope(Dispatchers.IO).launch {
            accDao.deleteAll()
            Log.d(TAG, "deleteAll() for ACC Data")
        }
    }
}