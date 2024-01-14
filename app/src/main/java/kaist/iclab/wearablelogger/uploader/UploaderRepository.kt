package kaist.iclab.wearablelogger.uploader

import android.content.Context
import android.util.Log
import androidx.room.Dao
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class UploaderRepository(
    private val androidContext: Context,
    private val daos: List<Dao>
) {
    private val TAG = javaClass.simpleName
    private val DATA_PATH = "/WEARABLE_DATA"
    fun sendData2Phone(){
        Log.d(TAG,"sendData2Phone")
        val dataClient = Wearable.getDataClient(androidContext)

        CoroutineScope(Dispatchers.IO).launch{
            try{
                val request = PutDataMapRequest.create(DATA_PATH).apply{
                    dataMap.putDataMapArrayList(DATA_PATH, arrayListOf())
                }.asPutDataRequest().setUrgent()
                dataClient.putDataItem(request).await()
                Log.d(TAG, "Data has been Uploaded")
            } catch(exception:Exception){
                Log.e(TAG, "Saving DataItem Failed: $exception")
            }

        }
    }
    fun jsonifyData():List<String> {
        return listOf()
    }
}