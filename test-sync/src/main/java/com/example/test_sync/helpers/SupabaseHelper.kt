package com.example.test_sync.helpers

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.postgrest.Postgrest
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

/**
 * Direct Supabase integration using supabase-kt library
 */
class SupabaseHelper {
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = AppConfig.SUPABASE_URL,
        supabaseKey = AppConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)  // Database operations
        install(Realtime)  // Real-time subscriptions
    }

    fun sendData(message: String, value: Int) {
        Log.d(AppConfig.LogTags.PHONE_SUPABASE, "🗄️ Sending data to Supabase - Message: '$message', Value: $value")
        val data = SupabaseData(
            message = message,
            value = value
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabaseClient.from(AppConfig.SUPABASE_TABLE_NAME).insert(data)
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "✅ Successfully inserted data to Supabase")
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_SUPABASE, "❌ Error sending data: ${e.message}")
            }
        }
    }

    fun sendTestData(testData: TestData) {
        Log.d(AppConfig.LogTags.PHONE_SUPABASE, "🗄️ Sending TestData to Supabase - Message: '${testData.message}', Value: ${testData.value}")
        val data = SupabaseData(
            message = testData.message,
            value = testData.value
        )
        CoroutineScope(Dispatchers.IO).launch {
            try {
                supabaseClient.from(AppConfig.SUPABASE_TABLE_NAME).insert(data)
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "✅ Successfully inserted TestData to Supabase")
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_SUPABASE, "❌ Error sending test data: ${e.message}")
            }
        }
    }

    fun getData() {
        Log.d(AppConfig.LogTags.PHONE_SUPABASE, "🗄️ Fetching data from Supabase")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = supabaseClient.from(AppConfig.SUPABASE_TABLE_NAME).select()
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "✅ Successfully fetched data from Supabase: $response")
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_SUPABASE, "❌ Error fetching data: ${e.message}")
            }
        }
    }
}