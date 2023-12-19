package kaist.iclab.wearablelogger

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.DataMapItem

class ClientDataViewModel:
    ViewModel(),
    DataClient.OnDataChangedListener {
    private val TAG = javaClass.simpleName
    private val _events = mutableStateListOf<Event>()
    val events: List<Event> = _events


    @SuppressLint("VisibleForTests")
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        Log.d(TAG, "DataItem saved: $dataEvents")

        // Add all events to the event log
        _events.addAll(
            dataEvents.map { dataEvent ->
                val title = when (dataEvent.type) {
                    DataEvent.TYPE_CHANGED -> "TYPE_CHANGED"
                    DataEvent.TYPE_DELETED -> "TYPE_DELETED"
                    else -> "UNKNOWN"
                }
                val host = dataEvent.dataItem.uri.host
                Log.d(TAG, host.toString())
                val data = DataMapItem.fromDataItem(dataEvent.dataItem).dataMap.getDataMapArrayList("data")

                val resData = convertDMLToAL(data) ?: "received data: NULL"
                Event(
                    title = title,
                    text = "${host}: ${resData.toString()}"
                )
            }
        )
    }
    private fun convertDMLToAL(dataMapList: ArrayList<DataMap>?): ArrayList<LongArray?>? {
        if (dataMapList.isNullOrEmpty()){
            Log.d(TAG, "Error : dataMapList is Null type")
            return null
        }
        val dataArrayList = ArrayList<LongArray?>()
        for (dataMap in dataMapList) {
            val al = dataMap.getLongArray("ppg")
            dataArrayList.add(al)
        }
        return dataArrayList
    }
}

data class Event(
    val title: String,
    val text: String
)
