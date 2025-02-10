package kaist.iclab.tracker.data.core

import kaist.iclab.tracker.collector.core.DataEntity
import kotlinx.coroutines.flow.StateFlow

interface DataStorageInterface {
    val NAME: String

    val statFlow: StateFlow<Pair<Long, Long>> /*timestamp & number*/

    fun insert(data: DataEntity)
    fun getUnsynced(): List<DataEntity>
    fun updateSyncStatus(ids: List<String>, timestamp: Long)
}