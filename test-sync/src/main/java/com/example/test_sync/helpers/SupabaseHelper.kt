package com.example.test_sync.helpers

import android.util.Log
import kaist.iclab.tracker.sync.supabase.createSupabaseClient
import kaist.iclab.tracker.sync.supabase.SupabaseDataChannel
import kaist.iclab.tracker.sync.supabase.SupabaseOperation
import kaist.iclab.tracker.sync.supabase.SupabaseResponse
import kotlinx.serialization.Serializable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.test_sync.TestData
import com.example.test_sync.config.AppConfig

@Serializable
data class SupabaseData(
    val id: Int? = null,
    val message: String,
    val value: Int,
    val created_at: String? = null
)

class SupabaseHelper {
    // Use the tracker library's Supabase client function
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = AppConfig.SUPABASE_URL,
        supabaseKey = AppConfig.SUPABASE_ANON_KEY
    )
    
    // Use the tracker library's Supabase data channel
    private val supabaseChannel = SupabaseDataChannel(supabaseClient)

    fun sendData(message: String, value: Int) {
        Log.d("PHONE_SUPABASE_SEND", "🗄️ Sending data to Supabase - Message: '$message', Value: $value")
        val data = SupabaseData(
            message = message,
            value = value
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = supabaseChannel.send(AppConfig.SUPABASE_TABLE_NAME, data, SupabaseOperation.INSERT)
            when (response) {
                is SupabaseResponse.Success -> {
                    Log.d("PHONE_SUPABASE_SEND", "✅ Successfully inserted data to Supabase")
                }
                is SupabaseResponse.Error -> {
                    Log.e("PHONE_SUPABASE_SEND", "❌ Supabase Error: ${response.message}")
                }
            }
        }
    }

    fun sendTestData(testData: TestData) {
        Log.d("PHONE_SUPABASE_SEND", "🗄️ Sending TestData to Supabase - Message: '${testData.message}', Value: ${testData.value}")
        val data = SupabaseData(
            message = testData.message,
            value = testData.value
        )
        CoroutineScope(Dispatchers.IO).launch {
            val response = supabaseChannel.send(AppConfig.SUPABASE_TABLE_NAME, data, SupabaseOperation.INSERT)
            when (response) {
                is SupabaseResponse.Success -> {
                    Log.d("PHONE_SUPABASE_SEND", "✅ Successfully inserted TestData to Supabase")
                }
                is SupabaseResponse.Error -> {
                    Log.e("PHONE_SUPABASE_SEND", "❌ Supabase Error: ${response.message}")
                }
            }
        }
    }

    fun getData() {
        Log.d("PHONE_SUPABASE_GET", "🗄️ Fetching data from Supabase")
        CoroutineScope(Dispatchers.IO).launch {
            val response = supabaseChannel.get(AppConfig.SUPABASE_TABLE_NAME)
            when (response) {
                is SupabaseResponse.Success -> {
                    Log.d("PHONE_SUPABASE_GET", "✅ Successfully fetched data from Supabase: ${response.data}")
                }
                is SupabaseResponse.Error -> {
                    Log.e("PHONE_SUPABASE_GET", "❌ Supabase Error: ${response.message}")
                }
            }
        }
    }
}
