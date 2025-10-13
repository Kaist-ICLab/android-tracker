package kaist.iclab.tracker.sync.supabase

import kaist.iclab.tracker.sync.core.DataChannelSender
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response

/**
 * Supabase data sender for sending data through HTTP to Supabase.
 * Uses HTTP POST requests to send data to Supabase endpoints.
 */
class SupabaseSender(
    private val supabaseUrl: String,
    private val supabaseKey: String
) : DataChannelSender<Response>() {
    
    private val client = OkHttpClient()

    override suspend fun send(key: String, value: String): Response {
        val url = "$supabaseUrl/$key"
        val jsonMediaType = "application/json; charset=utf-8".toMediaType()
        val requestBody = value.toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .addHeader("Authorization", "Bearer $supabaseKey")
            .addHeader("apikey", supabaseKey)
            .addHeader("Content-Type", "application/json")
            .build()

        return client.newCall(request).execute()
    }
}
