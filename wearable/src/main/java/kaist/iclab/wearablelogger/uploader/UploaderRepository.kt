//package kaist.iclab.wearablelogger.uploader
//
//import android.content.Context
//import android.util.Log
//import com.google.android.gms.wearable.PutDataMapRequest
//import com.google.android.gms.wearable.Wearable
//import kaist.iclab.wearablelogger.collector.CollectorInterface
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//
//class UploaderRepository(
//    private val androidContext: Context,
//) {
//    private val TAG = javaClass.simpleName
//    private val DATA_PATH = "/WEARABLE_DATA"
//    fun upload2Phone(data: String, key: String) {
//        Log.d(TAG, "sendData2Phone")
//        val dataClient = Wearable.getDataClient(androidContext)
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val request = PutDataMapRequest.create(DATA_PATH).run {
//                    dataMap.putString(key, data)
//                    asPutDataRequest()
//                }
//                dataClient.putDataItem(request).await()
//                Log.d(TAG, "Data has been uploaded")
//            } catch (exception: Exception) {
//                Log.e(TAG, "Saving DataItem failed: $exception")
//            }
//        }
//    }
//
//    suspend fun sync2Server(data: String) {
//        Log.d(TAG, "sync2Server")
//
//        val api = RetrofitClient.getRetrofit().create(ServerAPIInterface::class.java)
//        try{
//            api.postData(data).enqueue(object : Callback<String> {
//                override fun onResponse(call: Call<String>, response: Response<String>) {
//                    Log.d(TAG, response.message())
//                }
//
//                override fun onFailure(call: Call<String>, t: Throwable) {
//                    Log.d(TAG, "onFailure: ${t.message}")
//                }
//            })
//        }catch (e: Exception){
//            Log.e(TAG, "$e")
//        }
//
//    }
//}