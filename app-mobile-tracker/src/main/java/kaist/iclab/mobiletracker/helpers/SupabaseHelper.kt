package kaist.iclab.mobiletracker.helpers

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import kaist.iclab.mobiletracker.config.AppConfig

/**
 * Direct Supabase integration using the supabase-kt library
 */
class SupabaseHelper {
    private val supabaseClient = createSupabaseClient(
        supabaseUrl = AppConfig.SUPABASE_URL,
        supabaseKey = AppConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)  // Database operations
        install(Realtime)  // Real-time subscriptions
    }

    // fun sendData(message: String, value: Int) {
    //     Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Sending data: Message='$message', Value=$value")
    //     val data = SupabaseData(
    //         message = message,
    //         value = value
    //     )
    //     CoroutineScope(Dispatchers.IO).launch {
    //         try {
    //             val response = supabaseClient.from(AppConfig.SUPABASE_TABLE_NAME).insert(data)
    //             Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Data sent successfully")
    //             try {
    //                 val responseData = response.decodeList<SupabaseData>()
    //                 Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Response: $responseData")
    //             } catch (e: Exception) {
    //                 Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Response: $response")
    //             }
    //         } catch (e: Exception) {
    //             Log.e(AppConfig.LogTags.PHONE_SUPABASE, "Error sending data: ${e.message}")
    //         }
    //     }
    // }

    // fun getData() {
    //     Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Retrieving data from Supabase")
    //     CoroutineScope(Dispatchers.IO).launch {
    //         try {
    //             val response = supabaseClient.from(AppConfig.SUPABASE_TABLE_NAME).select()
    //             Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Data retrieved successfully")
    //             try {
    //                 val responseData = response.decodeList<SupabaseData>()
    //                 Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Response: $responseData")
    //             } catch (e: Exception) {
    //                 Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Response: $response")
    //             }
    //         } catch (e: Exception) {
    //             Log.e(AppConfig.LogTags.PHONE_SUPABASE, "Error fetching data: ${e.message}")
    //         }
    //     }
    // }
}
