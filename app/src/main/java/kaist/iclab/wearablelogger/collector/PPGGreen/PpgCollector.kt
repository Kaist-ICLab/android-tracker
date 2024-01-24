package kaist.iclab.wearablelogger.collector.PPGGreen


import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import com.google.gson.Gson
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerError
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
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
import org.koin.core.context.GlobalContext.get


class PpgCollector(
    val healthTrackerRepo: HealthTrackerRepo,
    val ppgDao: PpgDao,
    val toggleStates: ToggleStates
): AbstractCollector {
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

    override fun setup() {}
    override fun startLogging() {
        if (!toggleStates.ppgState) {
            return
        }
        Log.d(TAG, "startLogging")
        try {
            ppgGreenTracker = healthTrackerRepo.healthTrackingService.getHealthTracker(HealthTrackerType.PPG_GREEN)
            ppgGreenTracker?.setEventListener(trackerEventListener)
        } catch(e: Exception){
            Log.e(TAG, "PPGGreenCollector startLogging: ${e}")
        }
    }
    override fun stopLogging() {
        if (!toggleStates.ppgState) {
            return
        }
        Log.d(TAG, "stopLogging")
        ppgGreenTracker?.unsetEventListener()
    }
    override fun zip2prepareSend(): ArrayList<String> {
        val gson = Gson()
        val savedDataList: List<PpgEntity> = ppgDao.getAll()
        Log.d(TAG, "savedPpgDataList: ${savedDataList.toString()}")
        val jsonList = ArrayList<String>()
        savedDataList.forEach { ppgEntity ->
            val jsonStr = gson.toJson(ppgEntity)
            jsonList.add(jsonStr)
        }
        return jsonList
    }
    override fun flush() {
        Log.d(TAG, "Flush PPG Data")
        CoroutineScope(Dispatchers.IO).launch {
            ppgDao.deleteAll()
            Log.d(TAG, "deleteAll() for PPG Data")
        }
    }
}