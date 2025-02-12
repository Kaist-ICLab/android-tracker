package kaist.iclab.tracker.old

import android.content.Context
import com.google.android.gms.wearable.MessageClient
import com.google.android.gms.wearable.MessageEvent
import com.google.android.gms.wearable.Wearable
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/* Use for real-time monitoring, No sync offered */
class WatchPhoneMessageClientImpl(
    val context: Context
) : WatchPhoneInterface {
    private val MESSAGE_PATH = "/WEARABLE_MESSAGE"
    private val messageClient by lazy { Wearable.getMessageClient(context) }

    private val _messageFlow: MutableStateFlow<Map<String, String>> = MutableStateFlow(emptyMap())
    override val messageFlow: StateFlow<Map<String, String>> = _messageFlow.asStateFlow()


    override fun post(data: Map<String, String>) {
        val gson = Gson()
        Wearable.getNodeClient(context).connectedNodes.addOnSuccessListener {
            it.forEach { node ->
                messageClient.sendMessage(
                    node.id,
                    MESSAGE_PATH,
                    gson.toJson(data).toByteArray(Charsets.UTF_8)
                )
            }
        }
    }

    override fun startRetriever() {
        messageClient.addListener(retriever)
    }

    override fun stopRetriever() {
        messageClient.removeListener(retriever)
    }

    private val retriever = object : MessageClient.OnMessageReceivedListener {
        override fun onMessageReceived(messageEvent: MessageEvent) {
            if(messageEvent.path != MESSAGE_PATH) return
            val data = messageEvent.data.toString(Charsets.UTF_8)
            _messageFlow.value = Gson().fromJson(data, Map::class.java) as Map<String, String>
        }
    }
}