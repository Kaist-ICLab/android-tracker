package kaist.iclab.wearablelogger.collector.PPGGreen


import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerError
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepo
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kaist.iclab.wearablelogger.healthtracker.AbstractTrackerEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PpgCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val ppgDao: PpgDao,
): AbstractCollector() {
    private var ppgGreenTracker: HealthTracker? =  null
    override val TAG = javaClass.simpleName

    private val trackerEventListener: TrackerEventListener = object : AbstractTrackerEventListener() {
        override fun onDataReceived(data: List<DataPoint>) {
            val dataReceived = System.currentTimeMillis()
            val ppgData = data.map{
                PpgEntity(
                    dataReceived = dataReceived,
                    timestamp = it.timestamp,
                    ppg = it.getValue(ValueKey.PpgGreenSet.PPG_GREEN), //ADC value: might require DAC
                    status = it.getValue(ValueKey.PpgGreenSet.STATUS)
                )
            }
            CoroutineScope(Dispatchers.IO).launch{
                ppgDao.insertPpgEvents(ppgData)
            }
        }
    }
    override fun startLogging() {
        Log.d(TAG, "startLogging")
        try {
            ppgGreenTracker = healthTrackerRepo.healthTrackingService.getHealthTracker(HealthTrackerType.PPG_GREEN)
            ppgGreenTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "PPGGreenCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        ppgGreenTracker?.unsetEventListener()
    }
}