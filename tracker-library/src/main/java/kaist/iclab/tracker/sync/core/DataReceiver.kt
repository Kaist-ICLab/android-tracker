package kaist.iclab.tracker.sync.core

import kotlinx.serialization.json.JsonElement

/**
 * Interface for receiving data through various channels.
 */
interface DataReceiver {
    /**
     * Add a listener for specific keys
     */
    fun addOnReceivedListener(keys: Set<String>, callback: (String, JsonElement) -> Unit)

    /**
     * Remove a listener for specific keys
     */
    fun removeOnReceivedListener(keys: Set<String>, callback: (String, JsonElement) -> Unit)
}
