package kaist.iclab.tracker.data.core

import kaist.iclab.tracker.collectors.core.CollectorConfig
import kaist.iclab.tracker.collectors.core.CollectorState

interface ServerInterface {
    fun init()

    // Server IP
    fun checkServerConnection(): Boolean


    // Get Group Id and State&Config for remote setup
    fun getGroupIds(): List<String>
    fun getGroupStateNConfig(name: String): Map<String, Pair<CollectorState, CollectorConfig>>

    // Send to Server Database
    fun sync()
}
