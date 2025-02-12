package kaist.iclab.tracker.old//package kaist.iclab.tracker.data
//
//import android.util.Log
//import kaist.iclab.tracker.collector.core.CollectorConfig
//import kaist.iclab.tracker.collector.core.CollectorState
//import kaist.iclab.tracker.data.old.DatabaseInterface
//import kaist.iclab.tracker.data.old.ServerInterface
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import okhttp3.Call
//import okhttp3.Callback
//import okhttp3.MediaType.Companion.toMediaType
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import okhttp3.RequestBody
//import okhttp3.Response
//import okio.BufferedSink
//import okio.source
//import retrofit2.Retrofit
//import retrofit2.converter.gson.GsonConverterFactory
//import java.io.ByteArrayInputStream
//import java.io.ByteArrayOutputStream
//import java.io.IOException
//import java.util.zip.GZIPOutputStream
//
//class ServerInterfaceImpl(
//    val database: DatabaseInterface
//) : ServerInterface {
//
//    var retrofit: Retrofit? = null
//    var api: ServerRetrofitAPI? = null
//
//    // 로딩 상태를 관리하는 StateFlow
//    private val _loading = MutableStateFlow(false)
//    val loading: StateFlow<Boolean> get() = _loading
//
//    override fun init() {
//        database.serverAddressFlow.value?.let { address ->
//            retrofit = Retrofit.Builder()
//                .baseUrl(address)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build()
//            api = retrofit?.create(ServerRetrofitAPI::class.java)
//        }
//    }
//
//
//    override fun checkServerConnection(): Boolean {
//        _loading.value = true
//        return try {
//            if(api == null){
//                throw Exception("API is not initialized")
//            }else{
//                val response = api!!.checkServerConnection().execute()
//                response.isSuccessful
//            }
//        } catch (e: Exception) {
//            false
//        } finally {
//            _loading.value = false
//        }
//    }
//
//    override fun getGroupIds(): List<String> {
//        _loading.value = true
//        return try {
//            if(api == null){
//                throw Exception("API is not initialized")
//            }else{
//                val response = api!!.getGroupIds().execute()
//                response.body() ?: emptyList()
//            }
//        } catch (e: Exception) {
//            emptyList()
//        } finally {
//            _loading.value = false
//        }
//    }
//
//    override fun getGroupStateNConfig(name: String): Map<String, Pair<CollectorState, CollectorConfig>> {
//        return try {
//            if(api == null){
//                throw Exception("API is not initialized")
//            }else{
//                val response = api!!.getGroupStateNConfig(name).execute()
//                val result = response.body() ?: emptyMap()
//                // Map 변환: StateConfigResponse -> Pair<CollectorState, CollectorConfig>
//                result.mapValues {
//                    Pair(it.value.state, it.value.config)
//                }
//            }
//        } catch (e: Exception) {
//            emptyMap()
//        }
//    }
//
//    override fun sync() {
//        _loading.value = true
//        val (sendString, containedId) = database.db2Json(true)
//        val gzippedJson = gzipCompress(sendString)
//
//        val requestBody = object : RequestBody() {
//            override fun contentType() = "application/json; charset=utf-8".toMediaType()
//            override fun writeTo(sink: BufferedSink) {
//                val inputStream = ByteArrayInputStream(gzippedJson)
//                sink.writeAll(inputStream.source())
//            }
//        }
//        val syncUrl = database.serverAddressFlow.value
//        if(syncUrl == null){
//            throw Exception("Server address is not set")
//        }else{
//            val request = Request.Builder()
//                .url(syncUrl)
//                .addHeader("Content-Encoding", "gzip") // GZIP 압축임을 명시
//                .post(requestBody)
//                .build()
//
//            // 비동기 요청 실행
//            val client = OkHttpClient()
//            client.newCall(request).enqueue(object : Callback {
//                override fun onFailure(call: Call, e: IOException) {
//                    Log.e("ServerInterfaceImpl", "Failed to send data to server")
//                    _loading.value = false
//                }
//                override fun onResponse(call: Call, response: Response) {
//                    database.updateSyncStatus(containedId)
//                    _loading.value = false
//                }
//            })
//        }
//    }
//
//    private fun gzipCompress(data: String): ByteArray {
//        val outputStream = ByteArrayOutputStream()
//        GZIPOutputStream(outputStream).use { it.write(data.toByteArray()) }
//        return outputStream.toByteArray()
//    }
//}
