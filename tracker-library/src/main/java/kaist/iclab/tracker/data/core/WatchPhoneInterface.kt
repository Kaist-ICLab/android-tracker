package kaist.iclab.tracker.data.core

import kotlinx.coroutines.flow.StateFlow


interface WatchPhoneInterface {
    val messageFlow: StateFlow<Map<String, String>>
    fun post(data: Map<String, String>)

    fun startRetriever()
    fun stopRetriever()
}