package kaist.iclab.wearablelogger.collector.PPGGreen


import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kaist.iclab.wearablelogger.collector.HealthTrackerCollector
import kaist.iclab.wearablelogger.config.ConfigRepository
import kaist.iclab.wearablelogger.healthtracker.AbstractTrackerEventListener
import kaist.iclab.wearablelogger.healthtracker.HealthTrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PpgCollector(
    context: Context,
    private val healthTrackerRepository: HealthTrackerRepository,
    private val configRepository: ConfigRepository,
    private val ppgDao: PpgDao,
): HealthTrackerCollector(context) {
    override val TAG = javaClass.simpleName

    override val trackerEventListener = object : AbstractTrackerEventListener() {
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

    override fun initHealthTracker() {
        tracker = healthTrackerRepository.healthTrackingService
            .getHealthTracker(HealthTrackerType.PPG_GREEN)
    }

    override suspend fun getStatus(): Boolean {
        return configRepository.getSensorStatus("PPG Green")
    }

    override suspend fun stringifyData():String{
        val gson = GsonBuilder().setLenient().create()

        return gson.toJson(mapOf(javaClass.simpleName to ppgDao.getAll()))
    }
    override fun flush() {
        Log.d(TAG, "Flush PPG Data")
        CoroutineScope(Dispatchers.IO).launch {
            ppgDao.deleteAll()
            Log.d(TAG, "deleteAll() for PPG Data")
        }
    }
}