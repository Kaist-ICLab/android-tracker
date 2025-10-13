package kaist.iclab.tracker.sync.core

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * A bi-direction data channel that can be used to send and receive data.
 *
 * While using a single channel network, one can distinguish the type of data transferred by key.
 * 
 * This class now acts as a composite that combines a DataSender and DataReceiver.
 */
abstract class DataChannel<R> : DataSender<R>, DataReceiver {
    protected abstract val sender: DataSender<R>
    protected abstract val receiver: DataReceiver

    override suspend fun send(key: String, value: String): R {
        return sender.send(key, value)
    }

    override fun addOnReceivedListener(keys: Set<String>, callback: (String, JsonElement) -> Unit) {
        receiver.addOnReceivedListener(keys, callback)
    }

    override fun removeOnReceivedListener(keys: Set<String>, callback: (String, JsonElement) -> Unit) {
        receiver.removeOnReceivedListener(keys, callback)
    }
}
