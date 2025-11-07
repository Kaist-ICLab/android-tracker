package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kaist.iclab.wearabletracker.db.entity.SyncMetadataEntity

@Dao
interface SyncMetadataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(syncMetadata: SyncMetadataEntity)

    @Query("SELECT lastSyncTimestamp FROM SyncMetadataEntity WHERE id = 1")
    suspend fun getLastSyncTimestamp(): Long?

    @Query("SELECT EXISTS(SELECT 1 FROM SyncMetadataEntity WHERE id = 1)")
    suspend fun hasSyncRecord(): Boolean
}
