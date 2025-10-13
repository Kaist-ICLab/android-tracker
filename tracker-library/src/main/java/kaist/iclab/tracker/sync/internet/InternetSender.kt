package kaist.iclab.tracker.sync.internet

import kaist.iclab.tracker.sync.core.DataChannelSender
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/**
 * Internet data sender for sending data through HTTP.
 * Supports various HTTP methods for different use cases.
 */
class InternetSender : DataChannelSender<Response>() {
    enum class Method {
        GET,
        POST,
        PUT,
        PATCH,
        DELETE
    }

    private val client = OkHttpClient()

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
                when (method) {
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
}
