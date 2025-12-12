package kaist.iclab.mobiletracker.db.dao.watch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.WatchLocationEntity

@Dao
interface WatchLocationDao : BaseDao<WatchLocationEntity, WatchLocationEntity> {
    @Insert
    suspend fun insert(entities: List<WatchLocationEntity>)
    
    override suspend fun insert(sensorEntity: WatchLocationEntity, userUuid: String?) {
        insert(listOf(sensorEntity))
    }

    override suspend fun insertBatch(entities: List<WatchLocationEntity>, userUuid: String?) {
        insert(entities)
    }

    @Query("SELECT * FROM watch_location ORDER BY timestamp ASC")
    suspend fun getAllLocationData(): List<WatchLocationEntity>

    @Query("SELECT * FROM watch_location WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WatchLocationEntity>

    @Query("SELECT MAX(timestamp) FROM watch_location")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM watch_location")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM watch_location")
    override suspend fun deleteAll()
}

