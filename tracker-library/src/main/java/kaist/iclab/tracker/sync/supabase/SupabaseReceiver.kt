package kaist.iclab.tracker.sync.supabase

import kaist.iclab.tracker.sync.core.DataChannelReceiver

/**
 * Supabase data receiver for receiving real-time data from Supabase.
 * This is a placeholder implementation that would integrate with Supabase real-time subscriptions.
 *
 * Note: This is a simplified implementation. In a real scenario, you would need to:
 * 1. Set up Supabase real-time subscriptions
 * 2. Handle authentication
 * 3. Manage connection lifecycle
 * 4. Parse Supabase real-time message format
 */
internal class SupabaseReceiver(
    private val supabaseUrl: String,
    private val supabaseKey: String
) : DataChannelReceiver() {

    // TODO: Implement Supabase real-time subscription
    // This would typically involve:
    // - Setting up WebSocket connection to Supabase
    // - Subscribing to specific tables/channels
    // - Handling authentication
    // - Parsing incoming real-time messages

    fun startListening() {
        // TODO: Implement Supabase real-time subscription logic
        // Example:
        // 1. Create Supabase client
        // 2. Set up real-time subscription
        // 3. Handle incoming messages and call notifyCallbacks(key, value)
    }

    fun stopListening() {
        // TODO: Implement cleanup logic
        // Example:
        // 1. Unsubscribe from real-time channels
        // 2. Close WebSocket connections
    }
}
