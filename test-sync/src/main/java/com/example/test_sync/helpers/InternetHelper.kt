package com.example.test_sync.helpers

import android.util.Log
import kaist.iclab.tracker.sync.internet.InternetSender
import kaist.iclab.tracker.sync.internet.InternetSender.Method
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class InternetHelper {
    private val internetSender = InternetSender()

    fun sendGetRequest(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            Log.d("PHONE_INTERNET_SEND", "🌐 Sending GET request to server - URL: '$url'")
            try {
                val response = internetSender.send(url, "", Method.GET)
                val responseBody = response.body?.string() ?: "No response body"
                Log.d("PHONE_INTERNET_SEND", "GET Response (${response.code}): $responseBody")
            } catch (e: Exception) {
                Log.e("PHONE_INTERNET_SEND", "GET Error: ${e.message}")
            }
        }
    }

    fun sendPostRequest(url: String, value: TestData) {
        CoroutineScope(Dispatchers.IO).launch {
            val jsonString = Json.encodeToString(value)
            Log.d("PHONE_INTERNET_SEND", "🌐 Sending POST request to server - URL: '$url', Data: $jsonString")
            try {
                val response = internetSender.send(url, jsonString, Method.POST)
                val responseBody = response.body?.string() ?: "No response body"
                Log.d("PHONE_INTERNET_SEND", "POST Response (${response.code}): $responseBody")
            } catch (e: Exception) {
                Log.e("PHONE_INTERNET_SEND", "POST Error: ${e.message}")
            }
        }
    }
}
