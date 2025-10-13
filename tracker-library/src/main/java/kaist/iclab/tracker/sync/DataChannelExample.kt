package kaist.iclab.tracker.sync

import android.content.Context
import kaist.iclab.tracker.sync.ble.BLEDataChannel
import kaist.iclab.tracker.sync.core.DataReceiver
import kaist.iclab.tracker.sync.core.DataSender
import kaist.iclab.tracker.sync.internet.InternetDataChannel
import kaist.iclab.tracker.sync.supabase.SupabaseDataChannel
import kaist.iclab.tracker.sync.supabase.SupabaseReceiver
import kaist.iclab.tracker.sync.supabase.SupabaseSender
import kotlinx.serialization.Serializable

/**
 * Example demonstrating how to use the new separated DataChannel pattern.
 * This shows how to use different combinations of senders and receivers.
 */
class DataChannelExample {
    
    /**
     * Example 1: Symmetric BLE channel (both send and receive)
     */
    fun createSymmetricBLEChannel(context: Context): BLEDataChannel {
        return BLEDataChannel(context)
    }
    
    /**
     * Example 2: Asymmetric Internet channel (HTTP send, FCM receive)
     */
    fun createAsymmetricInternetChannel(): InternetDataChannel {
        return InternetDataChannel()
    }
    
    /**
     * Example 3: Asymmetric Supabase channel (HTTP send, real-time receive)
     */
    fun createAsymmetricSupabaseChannel(supabaseUrl: String, supabaseKey: String): SupabaseDataChannel {
        return SupabaseDataChannel(supabaseUrl, supabaseKey)
    }
    
    /**
     * Example 4: Using only a sender (for one-way communication)
     */
    fun createSenderOnly(supabaseUrl: String, supabaseKey: String): DataSender<okhttp3.Response> {
        return SupabaseSender(supabaseUrl, supabaseKey)
    }
    
    /**
     * Example 5: Using only a receiver (for listening only)
     */
    fun createReceiverOnly(supabaseUrl: String, supabaseKey: String): DataReceiver {
        return SupabaseReceiver(supabaseUrl, supabaseKey)
    }
    
    /**
     * Example usage of the channels
     */
    suspend fun exampleUsage(context: Context) {
        // Symmetric BLE channel
        val bleChannel = createSymmetricBLEChannel(context)
        bleChannel.addOnReceivedListener(setOf("sensor_data")) { key, value ->
            // Handle received BLE data
        }
        bleChannel.send("sensor_data", "some data")
        
        // Asymmetric Internet channel
        val internetChannel = createAsymmetricInternetChannel()
        internetChannel.addOnReceivedListener(setOf("notification")) { key, value ->
            // Handle received FCM notification
        }
        internetChannel.send("https://api.example.com/data", "some data")
        
        // Asymmetric Supabase channel
        val supabaseChannel = createAsymmetricSupabaseChannel(
            "https://your-project.supabase.co",
            "your-supabase-key"
        )
        supabaseChannel.startListening()
        supabaseChannel.addOnReceivedListener(setOf("realtime_update")) { key, value ->
            // Handle real-time updates from Supabase
        }
        supabaseChannel.send("table_name", "some data")
    }
}