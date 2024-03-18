package kaist.iclab.wearablelogger

import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import kaist.iclab.wearablelogger.db.RecentDao
import kaist.iclab.wearablelogger.db.RecentEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DataReceiver(
    val recentDao: RecentDao
) : DataClient.OnDataChangedListener {
    private val TAG = javaClass.simpleName

    override fun onDataChanged(dataEventBuffer: DataEventBuffer) {
        Log.d(TAG, "onDataChanged Called!")
        val recentEntities = dataEventBuffer.map { dataEvent ->
            Log.d(TAG,dataEvent.dataItem.uri.toString())
            val data = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap
            Log.d(TAG, data.toString())
            RecentEntity(
                timestamp = data.getLong("timestamp"),
                acc = data.getString("acc")?:"null",
                ppg = data.getString("ppg")?:"null",
                hr = data.getString("hr")?:"null"
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            recentDao.insertEvents(
                recentEntities
            )
        }

    }
}