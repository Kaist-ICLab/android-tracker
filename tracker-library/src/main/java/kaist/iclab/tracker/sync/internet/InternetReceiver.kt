package kaist.iclab.tracker.sync.internet

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kaist.iclab.tracker.sync.core.DataChannelReceiver
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

/**
 * Internet data receiver for receiving data through Firebase Cloud Messaging.
 * Uses FCM to receive real-time messages from the server.
 */
class InternetReceiver(
    private val keyParamName: String = "_key"
) : DataChannelReceiver() {
    private val newTokenListener: MutableList<(String) -> Unit> = mutableListOf()

    private val mainOnNewTokenCallback = { token: String ->
        newTokenListener.forEach { it.invoke(token) }
    }

    private val mainOnReceivedCallback = { key: String, value: JsonElement ->
        notifyCallbacks(key, value)
    }

    init {
        FCMService.onNewTokenListeners = mainOnNewTokenCallback
        FCMService.onMessageReceivedListeners = mainOnReceivedCallback
        FCMService.keyParamName = keyParamName
        FCMService.callbackList = callbackList
    }

    fun addOnNewFirebaseTokenListener(callback: (String) -> Unit) {
        newTokenListener.add(callback)
    }

    fun removeOnNewFirebaseTokenListener(callback: (String) -> Unit) {
        newTokenListener.remove(callback)
    }

    class FCMService : FirebaseMessagingService() {
        companion object {
            var onNewTokenListeners: (String) -> Unit = {}
            var onMessageReceivedListeners: (String, JsonElement) -> Unit? = { _, _ -> }
            var keyParamName: String? = null
            var callbackList = mutableMapOf<String, MutableList<(String, JsonElement) -> Unit>>()
        }

        override fun onNewToken(token: String) {
            super.onNewToken(token)
            onNewTokenListeners(token)
        }

        override fun onMessageReceived(message: RemoteMessage) {
            super.onMessageReceived(message)

            val payload = message.data
            val key = payload[keyParamName] ?: ""

            val jsonString = Json.encodeToString(payload)
            onMessageReceivedListeners(key, Json.parseToJsonElement(jsonString))
        }
    }
}
