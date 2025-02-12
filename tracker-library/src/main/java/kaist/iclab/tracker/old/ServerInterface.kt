package kaist.iclab.tracker.old

import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState

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
