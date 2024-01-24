package kaist.iclab.wearablelogger.uploader

import android.content.Context
import android.util.Log
import androidx.room.Dao
import com.google.android.gms.wearable.DataMap
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kaist.iclab.wearablelogger.collector.AbstractCollector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UploaderRepository(
    val collectors: List<AbstractCollector>,
    private val androidContext: Context,
//    private val daos: List<Dao>
) {
    private val TAG = javaClass.simpleName
    private val DATA_PATH = "/WEARABLE_DATA"
    fun sendData2Phone(){
        Log.d(TAG,"sendData2Phone")
        val dataClient = Wearable.getDataClient(androidContext)
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dataMapArrayList = ArrayList<DataMap> ()
                val keys = listOf("ppg", "acc", "hr", "st")
                collectors.onEach { collector ->
                    val index = collectors.indexOf(collector)
                    val dataList = collector.zip2prepareSend()
                    val dataMap = DataMap().apply {
                        putStringArrayList(keys[index], dataList)
                    }
                    dataMapArrayList.add(dataMap)
                    Log.d(TAG, "dataMap for ${keys[index]}: $dataMap")
                }
                val request = PutDataMapRequest.create(DATA_PATH).apply {
                    dataMap.putDataMapArrayList(DATA_PATH, dataMapArrayList)
                }.asPutDataRequest().setUrgent()

                dataClient.putDataItem(request).await()
                Log.d(TAG, "Data has been uploaded")
            } catch (exception: Exception) {
                Log.e(TAG, "Saving DataItem failed: $exception")
            }
        }
    }
    fun jsonifyData():List<String> {
        return listOf()
    }
}