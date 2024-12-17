package kaist.iclab.tracker.data.core

import kaist.iclab.tracker.collectors.core.CollectorConfig
import kaist.iclab.tracker.collectors.core.CollectorState
import kaist.iclab.tracker.collectors.core.DataEntity
import kotlinx.coroutines.flow.StateFlow

interface DatabaseInterface {
    // Server IP
    val serverAddressFlow: StateFlow<String?>
    fun registerServer(serverAddress: String)

    // For Sync
    fun db2Json(unsyncedOnly: Boolean): Pair<String, List<String>>
    fun updateSyncStatus(ids: List<String>)

    // Local Database for Collector Config
    fun updateCollectorConfig(name: String, config: CollectorConfig)
    val collectorConfigFlow: StateFlow<Map<String, String>>

    // Local Database for Collector State
    fun updateCollectorState(name: String, state: CollectorState)
    val collectorStateFlow: StateFlow<Map<String, String>>

    // Local Database for Data Entity
    fun insert(name: String, data: DataEntity)
    fun update(name: String, id: String, data: DataEntity)
    fun delete(name: String, id: String)

    // Export data as zip file
    fun export(outputDirPath: String)

    // System Running Logging
    fun log(tag: String, message: String)
}
