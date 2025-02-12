package kaist.iclab.tracker.data.core

import kaist.iclab.tracker.collector.core.DataEntity
import kotlinx.coroutines.flow.StateFlow

interface DataStorage {
    val NAME: String
    val statFlow: StateFlow<DataStat>
    fun insert(data: DataEntity)
}

//    fun getUnsynced(): List<DataEntity>
//    fun updateSyncStatus(ids: List<String>, timestamp: Long)