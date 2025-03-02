package kaist.iclab.tracker.storage.core

import kaist.iclab.tracker.sensor.core.SensorEntity
import kotlinx.coroutines.flow.StateFlow

interface SensorDataStorage {
    val ID: String
    val statFlow: StateFlow<DataStat>
    fun insert(data: SensorEntity)
}

//    fun getUnsynced(): List<DataEntity>
//    fun updateSyncStatus(ids: List<String>, timestamp: Long)