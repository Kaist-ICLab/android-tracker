package kaist.iclab.tracker.old

import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.core.DataEntity
import kotlinx.coroutines.flow.StateFlow

interface DatabaseInterface {
    // Sync-settings
    val syncConfigFlow: StateFlow<SyncConfig>
    fun updateSyncConfig(syncConfig: SyncConfig)

    // Local Database for Collector Config
    fun updateCollectorConfig(name: String, config: CollectorConfig)
    fun getCollectorConfigFlow(name: String): StateFlow<CollectorConfig>

    // Local Database for Collector State
    fun updateCollectorState(name: String, state: CollectorState)
    fun getCollectorStateFlow(name: String): StateFlow<CollectorState>

    // Local Database for Data Entity
    fun insert(name: String, data: DataEntity)
    fun update(name: String, id: String, data: DataEntity)
    fun delete(name: String, id: String)
    fun updateSyncStatus(ids: List<String>)
}

/*
*     // For Sync-only
    fun updateSyncStatus(ids: List<String>)
    fun db2Json(unsyncedOnly: Boolean): Pair<String, List<String>>
*
* */
//    // Export data as zip file
//    fun export(outputDirPath: String)
//
//    // System Running Logging
//    fun log(tag: String, message: String)