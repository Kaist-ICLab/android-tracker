package kaist.iclab.tracker.data.old

import kotlinx.coroutines.flow.StateFlow


interface WatchPhoneInterface {
    val messageFlow: StateFlow<Map<String, String>>
    fun post(data: Map<String, String>)

    fun startRetriever()
    fun stopRetriever()
}