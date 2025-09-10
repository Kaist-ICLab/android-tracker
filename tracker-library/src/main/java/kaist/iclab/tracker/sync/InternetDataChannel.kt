package kaist.iclab.tracker.sync

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/**
 * A DataChannel that uses internet to transfer data.
 * Suitable for communication between server and clients, such as a mobile phone or a smartwatch.
 * If you are using HTTP connection, you have to allow cleartextTraffic in the Android Manifest. Please refer to test-sync module.
 *
 * One distinct characteristics of InternetDataChannel is that send() cam be used in 2 ways: for transferring data and for making a request.
 * In both cases, you can retrieve the response.
 *
 * Because maintaining socket connection on the background requires a lot of resource, we use Firebase Cloud Messaging (FCM) to receive messages.
 * This means that if you want to send information from the server to client, you need to use FCM.
 */
class InternetDataChannel(
    private val keyParamName: String = "_key"
): DataChannel<Response>() {
    enum class Method {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    private val client = OkHttpClient()

    suspend inline fun<reified T: @Serializable Any> send (key: String, value: T, method: Method): Response {
        return send(key, Json.encodeToString(value), method)
    }

    override suspend fun send(key: String, value: String): Response {
        return send(key, value, Method.POST)
    }

    suspend fun send(key: String, value: String, method: Method): Response {
        val url = key
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = value.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .apply {
                when(method) {
                    Method.GET -> get()
                    Method.POST -> post(requestBody)
                    Method.PUT -> put(requestBody)
                    Method.PATCH -> patch(requestBody)
                    Method.DELETE -> delete(requestBody)
                }
            }
            .build()

        return client.newCall(request).execute()
    }

    private val newTokenListener: MutableList<(String) -> Unit> = mutableListOf()

    private val mainOnNewTokenCallback = { token: String ->
        newTokenListener.forEach { it.invoke(token) }
    }

    private val mainOnReceivedCallback = { key: String, value: JsonElement ->
        callbackList[key]?.forEach { it.invoke(key, value) }
    }

    init {
        FCMService.onNewTokenListeners = mainOnNewTokenCallback
        FCMService.onMessageReceivedListeners = mainOnReceivedCallback
        FCMService.keyParamName = keyParamName
    }

    fun addOnNewFirebaseTokenListener(callback: (String) -> Unit) {
        newTokenListener.add(callback)
    }

    fun removeOnNewFirebaseTokenListener(callback: (String) -> Unit) {
        newTokenListener.remove(callback)
    }

    class FCMService: FirebaseMessagingService() {
        companion object {
            var onNewTokenListeners: (String) -> Unit = {}
            var onMessageReceivedListeners: (String, JsonElement) -> Unit? = { _, _ -> }
            var keyParamName: String? = null
        }

        override fun onNewToken(token: String) {
            super.onNewToken(token)
            onNewTokenListeners(token)
        }

        override fun onMessageReceived(message: RemoteMessage) {
            super.onMessageReceived(message)

            val payload = message.data
            val key = payload[keyParamName] ?: ""

            val jsonString = Json.encodeToString(payload)
            onMessageReceivedListeners(key, Json.parseToJsonElement(jsonString))
        }
    }
}