package kaist.iclab.wearablelogger.collector.SkinTemp

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.wearablelogger.ToggleStates
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.healthtracker.AbstractTrackerEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SkinTempCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val skinTempDao: SkinTempDao,
    val toggleStates: ToggleStates
): AbstractCollector {

    private var SkinTempTracker: HealthTracker? =  null
    override val TAG = javaClass.simpleName

    private val trackerEventListener: HealthTracker.TrackerEventListener = object :
        AbstractTrackerEventListener() {
        override fun onDataReceived(data: List<DataPoint>) {
            val dataReceived = System.currentTimeMillis()
            val skinTempData = data.map{
                SkinTempEntity(
                    dataReceived = dataReceived,
                    timestamp = it.timestamp,
                    ambientTemp = it.getValue(ValueKey.SkinTemperatureSet.AMBIENT_TEMPERATURE),
                    objectTemp = it.getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE),
                    status = it.getValue(ValueKey.SkinTemperatureSet.STATUS)
                )
            }
            CoroutineScope(Dispatchers.IO).launch{
                skinTempDao.insertSkinTempEvents(skinTempData)
            }
        }
    }
    override fun setup() {}
    override fun startLogging() {
        if (!toggleStates.stState) {
            return
        }
        Log.d(TAG, "startLogging")
        try {
            SkinTempTracker = healthTrackerRepo.healthTrackingService.getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS)
            SkinTempTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "SkinTempCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        if (!toggleStates.stState) {
            return
        }
        Log.d(TAG, "stopLogging")
        SkinTempTracker?.unsetEventListener()
    }
    override fun flush() {
        Log.d(TAG, "Flush SkinTemp Data")
        CoroutineScope(Dispatchers.IO).launch {
            skinTempDao.deleteAll()
            Log.d(TAG, "deleteAll() for SkinTemp Data")
        }
    }
}