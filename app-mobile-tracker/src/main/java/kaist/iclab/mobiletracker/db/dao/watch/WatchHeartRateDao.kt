package kaist.iclab.mobiletracker.db.dao.watch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity

@Dao
interface WatchHeartRateDao : BaseDao<WatchHeartRateEntity, WatchHeartRateEntity> {
    @Insert
    suspend fun insert(entities: List<WatchHeartRateEntity>)
    
    override suspend fun insert(sensorEntity: WatchHeartRateEntity) {
        insert(listOf(sensorEntity))
    }

    override suspend fun insertBatch(entities: List<WatchHeartRateEntity>) {
        insert(entities)
    }

    @Query("SELECT * FROM watch_heart_rate ORDER BY timestamp ASC")
    suspend fun getAllHeartRateData(): List<WatchHeartRateEntity>

    @Query("SELECT * FROM watch_heart_rate WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WatchHeartRateEntity>

    @Query("SELECT MAX(timestamp) FROM watch_heart_rate")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM watch_heart_rate")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM watch_heart_rate")
    override suspend fun deleteAll()
}

