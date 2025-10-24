package kaist.iclab.wearabletracker.storage

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonObject
import kaist.iclab.tracker.listener.SingleAlarmListener
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.wearabletracker.db.dao.BaseDao
import kaist.iclab.wearabletracker.storage.JsonFormatUtil.formatForUpload
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class DataSyncRepository(
    context: Context,
    private val storages: Map<String, BaseDao<*, *>>
) {
    companion object {
        private val TAG = DataSyncRepository::class.simpleName
    }

    private val gson = Gson()
    private val singleAlarmListener = SingleAlarmListener(
        context,
        actionName = "kaist.iclab.wearableTracker.REQUEST_SYNC",
        actionCode = 0x20253591
    )

    private val bleDataChannel = BLEDataChannel(context)

    private val dataSender = { _: Intent? ->
        Log.d(TAG, "dataSender invoked")
        CoroutineScope(Dispatchers.IO).launch {
            val endTime = System.currentTimeMillis()
            val startTime = endTime - TimeUnit.HOURS.toMillis(1)

            val deferredData = storages.map { (id, storage) ->
                async {
                    val result = storage.getSummaryBetween(startTime, endTime)
                    val data = gson.toJsonTree(result).asJsonArray
                    data.map { it.asJsonObject.formatForUpload() }
                    id to data
                }
            }

            val resultObject = JsonObject()
            deferredData.awaitAll().forEach { (id, data) ->
                resultObject.add(id, data)
            }

            bleDataChannel.send("data", resultObject.toString())
        }

        singleAlarmListener.scheduleNextAlarm(TimeUnit.MINUTES.toMillis(5), true)
    }

    fun startSending() {
        Log.d(TAG, "startSending()")
        singleAlarmListener.addListener(dataSender)
        dataSender(null)
    }

    fun stopSending() {
        Log.d(TAG, "stopSending()")
        singleAlarmListener.removeListener(dataSender)
    }
}
