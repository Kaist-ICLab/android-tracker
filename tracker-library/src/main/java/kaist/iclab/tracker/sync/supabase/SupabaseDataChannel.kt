package kaist.iclab.tracker.sync.supabase

import io.github.jan.supabase.SupabaseClient
import kaist.iclab.tracker.sync.core.DataChannel
import kaist.iclab.tracker.sync.core.DataChannelReceiver
import kaist.iclab.tracker.sync.core.DataReceiver
import kaist.iclab.tracker.sync.core.DataSender

/**
 * A DataChannel that uses Supabase to transfer data.
 * Suitable for database operations between the client and Supabase backend.
 * 
 * This class follows the same pattern as BLEDataChannel and InternetDataChannel.
 */
class SupabaseDataChannel(
    private val supabaseClient: SupabaseClient
): DataChannel<SupabaseResponse>() {
    
    override val sender: DataSender<SupabaseResponse> = SupabaseSender(supabaseClient)
    override val receiver: DataReceiver = object : DataChannelReceiver() {
        // No-op receiver - SupabaseDataChannel is send-only for now
        // TODO: Real-time subscriptions can be added later if needed
    }

    /**
     * Send data to the Supabase table
     */
    suspend fun send(tableName: String, data: Any, operation: SupabaseOperation): SupabaseResponse {
        return (sender as SupabaseSender).send(tableName, data, operation)
    }

    /**
     * Get data from the Supabase table
     */
    suspend fun get(tableName: String): SupabaseResponse {
        return (sender as SupabaseSender).get(tableName)
    }
}