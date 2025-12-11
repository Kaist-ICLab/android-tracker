package kaist.iclab.mobiletracker.db.dao.watch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity

@Dao
interface WatchSkinTemperatureDao : BaseDao<WatchSkinTemperatureEntity, WatchSkinTemperatureEntity> {
    @Insert
    suspend fun insert(entities: List<WatchSkinTemperatureEntity>)
    
    override suspend fun insert(sensorEntity: WatchSkinTemperatureEntity) {
        insert(listOf(sensorEntity))
    }

    override suspend fun insertBatch(entities: List<WatchSkinTemperatureEntity>) {
        insert(entities)
    }

    @Query("SELECT * FROM watch_skin_temperature ORDER BY timestamp ASC")
    suspend fun getAllSkinTemperatureData(): List<WatchSkinTemperatureEntity>

    @Query("SELECT * FROM watch_skin_temperature WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WatchSkinTemperatureEntity>

    @Query("SELECT MAX(timestamp) FROM watch_skin_temperature")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM watch_skin_temperature")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM watch_skin_temperature")
    override suspend fun deleteAll()
}

