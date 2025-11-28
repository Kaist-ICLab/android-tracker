package kaist.iclab.mobiletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.google.gson.Gson
import kaist.iclab.mobiletracker.db.entity.AppListChangeEntity
import kaist.iclab.tracker.sensor.phone.AppListChangeSensor

@Dao
interface AppListChangeDao: BaseDao<AppListChangeSensor.Entity> {
    companion object {
        private val gson = Gson()
    }

    override suspend fun insert(sensorEntity: AppListChangeSensor.Entity) {
        val entity = AppListChangeEntity(
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            changedAppJson = sensorEntity.changedApp?.let { gson.toJson(it) },
            appListJson = sensorEntity.appList?.let { gson.toJson(it) }
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(appListChangeEntity: AppListChangeEntity)

    @Query("SELECT * FROM AppListChangeEntity ORDER BY timestamp ASC")
    suspend fun getAllAppListChangeData(): List<AppListChangeEntity>

    @Query("DELETE FROM AppListChangeEntity")
    suspend fun deleteAllAppListChangeData()

    override suspend fun deleteAll() {
        deleteAllAppListChangeData()
    }
}

