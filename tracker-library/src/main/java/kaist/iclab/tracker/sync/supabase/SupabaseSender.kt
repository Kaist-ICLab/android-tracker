package kaist.iclab.tracker.sync.supabase

import android.util.Log
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kaist.iclab.tracker.sync.core.DataSender
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Supabase sender for database operations.
 * Follows the DataSender pattern for consistency with BLE and Internet channels.
 * 
 * Internal class - only accessible through SupabaseDataChannel.
 */
internal class SupabaseSender(
    private val supabaseClient: SupabaseClient
) : DataSender<SupabaseResponse> {

    /**
     * Required implementation of the DataSender interface
     */
    override suspend fun send(key: String, value: String): SupabaseResponse {
        return send(key, value, SupabaseOperation.INSERT)
    }

    /**
     * Send data to Supabase table
     */
    suspend fun send(tableName: String, data: Any, operation: SupabaseOperation): SupabaseResponse {
        return try {
            Log.d("SUPABASE_SENDER", "🗄️ Sending $operation to table '$tableName'")
            
            val result = when (operation) {
                SupabaseOperation.INSERT -> {
                    supabaseClient.from(tableName).insert(data)
                    "Inserted successfully"
                }
                SupabaseOperation.UPDATE -> {
                    supabaseClient.from(tableName).update(data)
                    "Updated successfully"
                }
                SupabaseOperation.DELETE -> {
                    supabaseClient.from(tableName).delete()
                    "Deleted successfully"
                }
            }
            
            Log.d("SUPABASE_SENDER", "✅ $result for table '$tableName'")
            SupabaseResponse.Success(result)
        } catch (e: Exception) {
            Log.e("SUPABASE_SENDER", "❌ Error $operation to table '$tableName': ${e.message}")
            SupabaseResponse.Error(e.message ?: "Unknown error")
        }
    }

    /**
     * Get data from Supabase table
     */
    suspend fun get(tableName: String): SupabaseResponse {
        return try {
            Log.d("SUPABASE_SENDER", "🗄️ Getting data from table '$tableName'")
            val response = supabaseClient.from(tableName).select()
            Log.d("SUPABASE_SENDER", "✅ Successfully got data from '$tableName'")
            SupabaseResponse.Success(response.toString())
        } catch (e: Exception) {
            Log.e("SUPABASE_SENDER", "❌ Error getting data from '$tableName': ${e.message}")
            SupabaseResponse.Error(e.message ?: "Unknown error")
        }
    }
}

/**
 * Supabase operation types
 */
enum class SupabaseOperation {
    INSERT, UPDATE, DELETE
}

/**
 * Supabase response wrapper
 */
sealed class SupabaseResponse {
    data class Success(val data: String) : SupabaseResponse()
    data class Error(val message: String) : SupabaseResponse()
}
