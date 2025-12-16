package kaist.iclab.mobiletracker.db.dao.watch

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.watch.WatchEDAEntity

@Dao
interface WatchEDADao : BaseDao<WatchEDAEntity, WatchEDAEntity> {
    @Insert
    suspend fun insert(entities: List<WatchEDAEntity>)
    
    override suspend fun insert(sensorEntity: WatchEDAEntity, userUuid: String?) {
        insert(listOf(sensorEntity))
    }

    override suspend fun insertBatch(entities: List<WatchEDAEntity>, userUuid: String?) {
        insert(entities)
    }

    @Query("SELECT * FROM watch_eda ORDER BY timestamp ASC")
    suspend fun getAllEDAData(): List<WatchEDAEntity>

    @Query("SELECT * FROM watch_eda WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<WatchEDAEntity>

    @Query("SELECT MAX(timestamp) FROM watch_eda")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM watch_eda")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM watch_eda")
    override suspend fun deleteAll()
}

