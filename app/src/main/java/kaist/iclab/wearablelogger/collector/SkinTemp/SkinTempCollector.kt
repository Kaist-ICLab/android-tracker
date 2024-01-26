package kaist.iclab.wearablelogger.collector.SkinTemp

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

class SkinTempCollector(
    context: Context,
    private val healthTrackerRepository: HealthTrackerRepository,
    private val configRepository: ConfigRepository,
    private val skinTempDao: SkinTempDao
) : HealthTrackerCollector(context) {
    override val TAG = javaClass.simpleName
    override fun initHealthTracker() {
        tracker = healthTrackerRepository.healthTrackingService
            .getHealthTracker(HealthTrackerType.SKIN_TEMPERATURE_CONTINUOUS)
    }

    override suspend fun getStatus(): Boolean {
        return configRepository.getSensorStatus("Skin Temperature")
    }

    override val trackerEventListener = object :
        AbstractTrackerEventListener() {
        override fun onDataReceived(data: List<DataPoint>) {
            val dataReceived = System.currentTimeMillis()
            val skinTempData = data.map {
                SkinTempEntity(
                    dataReceived = dataReceived,
                    timestamp = it.timestamp,
                    ambientTemp = it.getValue(ValueKey.SkinTemperatureSet.AMBIENT_TEMPERATURE),
                    objectTemp = it.getValue(ValueKey.SkinTemperatureSet.OBJECT_TEMPERATURE),
                    status = it.getValue(ValueKey.SkinTemperatureSet.STATUS)
                )
            }
            CoroutineScope(Dispatchers.IO).launch {
                skinTempDao.insertSkinTempEvents(skinTempData)
            }
        }
    }
    override suspend fun stringifyData():String{
        val gson = GsonBuilder().setLenient().create()
        return gson.toJson(mapOf(javaClass.simpleName to skinTempDao.getAll()))
    }

    override fun flush() {
        Log.d(TAG, "Flush SkinTemp Data")
        CoroutineScope(Dispatchers.IO).launch {
            skinTempDao.deleteAll()
            Log.d(TAG, "deleteAll() for SkinTemp Data")
        }
    }
}