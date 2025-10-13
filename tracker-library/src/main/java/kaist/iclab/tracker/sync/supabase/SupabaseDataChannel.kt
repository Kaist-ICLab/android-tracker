package kaist.iclab.tracker.sync.supabase

import kaist.iclab.tracker.sync.core.DataChannel
import kaist.iclab.tracker.sync.core.DataReceiver
import kaist.iclab.tracker.sync.core.DataSender
import okhttp3.Response

/**
 * A DataChannel that uses Supabase for data transfer.
 * 
 * This is an asymmetric channel:
 * - Sending: Uses HTTP POST requests to Supabase endpoints
 * - Receiving: Uses Supabase real-time subscriptions
 * 
 * This class uses the separated sender/receiver pattern.
 */
class SupabaseDataChannel(
    private val supabaseUrl: String,
    private val supabaseKey: String
): DataChannel<Response>() {
    
    override val sender: DataSender<Response> = SupabaseSender(supabaseUrl, supabaseKey)
    override val receiver: DataReceiver = SupabaseReceiver(supabaseUrl, supabaseKey)

    /**
     * Start listening for real-time updates from Supabase
     */
    fun startListening() {
        (receiver as SupabaseReceiver).startListening()
    }

    /**
     * Stop listening for real-time updates from Supabase
     */
    fun stopListening() {
        (receiver as SupabaseReceiver).stopListening()
    }
}
