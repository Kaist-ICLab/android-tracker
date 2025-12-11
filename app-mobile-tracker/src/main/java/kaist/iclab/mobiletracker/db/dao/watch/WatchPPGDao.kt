package kaist.iclab.mobiletracker.db.dao.watch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity

@Dao
interface WatchPPGDao : BaseDao<WatchPPGEntity, WatchPPGEntity> {
    @Insert
    suspend fun insert(entities: List<WatchPPGEntity>)
    
    override suspend fun insert(sensorEntity: WatchPPGEntity) {
        insert(listOf(sensorEntity))
    }

    override suspend fun insertBatch(entities: List<WatchPPGEntity>) {
        insert(entities)
    }

    @Query("SELECT * FROM watch_ppg ORDER BY timestamp ASC")
    suspend fun getAllPPGData(): List<WatchPPGEntity>

    @Query("SELECT * FROM watch_ppg WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WatchPPGEntity>

    @Query("SELECT MAX(timestamp) FROM watch_ppg")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM watch_ppg")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM watch_ppg")
    override suspend fun deleteAll()
}

