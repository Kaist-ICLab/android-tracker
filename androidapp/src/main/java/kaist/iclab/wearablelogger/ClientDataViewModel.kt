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
                Log.d("debuggingDataType", "datamapAL: ${data}")
                Log.d("debuggingDataType", "AL: ${resData}")
                Event(
                    title = title,
                    text = "${host}: ${resData}"
                )
            }
        )
    }
    private fun convertDMLToAL(dataMapList: ArrayList<DataMap>?): ArrayList<List<Long>?>? {
        if (dataMapList==null){
            Log.d(TAG, "Error : dataMapList is Null type")
            return null
        }
        val dataArrayList = ArrayList<List<Long>?>()
//        for (dataMap in dataMapList) {
//            val ppgList = dataMap.getLongArray("ppg")
//            val accList = dataMap.getLongArray("acc")
//            val hrList = dataMap.getLongArray("hr")
//            val stList = dataMap.getLongArray("skintemp")
//
//            dataArrayList.add(ppgList?.toList())
//            dataArrayList.add(accList?.toList())
//            dataArrayList.add(hrList?.toList())
//            dataArrayList.add(stList?.toList())
//        }
        for (dataMap in dataMapList) {
            val ppgList = dataMap.getLongArray("ppg")?.toList()
            val accList = dataMap.getLongArray("acc")?.toList()
            val hrList = dataMap.getLongArray("hr")?.toList()
            val stList = dataMap.getLongArray("skintemp")?.toList()

            ppgList?.let { dataArrayList.add(it) }
            accList?.let { dataArrayList.add(it) }
            hrList?.let { dataArrayList.add(it) }
            stList?.let { dataArrayList.add(it) }
        }
        return dataArrayList
    }
}

data class Event(
    val title: String,
    val text: String
)
