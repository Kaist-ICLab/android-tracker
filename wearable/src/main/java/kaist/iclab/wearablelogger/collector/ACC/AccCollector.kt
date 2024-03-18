package kaist.iclab.wearablelogger.collector.ACC

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
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

class AccCollector(
    context: Context,
    private val healthTrackerRepository: HealthTrackerRepository,
    private val configRepository: ConfigRepository,
    private val accDao: AccDao,
) : HealthTrackerCollector(context){
    override val TAG = javaClass.simpleName

    override val trackerEventListener: TrackerEventListener = object :
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

    override fun initHealthTracker() {
        tracker = healthTrackerRepository.healthTrackingService
            .getHealthTracker(HealthTrackerType.ACCELEROMETER)
    }

    override suspend fun getStatus(): Boolean {
        return configRepository.getSensorStatus("Accelerometer")
    }
    override suspend fun stringifyData():String{
        val gson = GsonBuilder().setLenient().create()

        return gson.toJson(mapOf(javaClass.simpleName to accDao.getAll()))
    }

    override fun flush() {
        Log.d(TAG, "Flush ACC Data")
        CoroutineScope(Dispatchers.IO).launch {
            accDao.deleteAll()
            Log.d(TAG, "deleteAll() for ACC Data")
        }
    }
}