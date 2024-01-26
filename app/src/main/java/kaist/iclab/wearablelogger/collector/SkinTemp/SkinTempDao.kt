package kaist.iclab.wearablelogger.collector.SkinTemp

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface SkinTempDao {
    @Query("SELECT * FROM skinTempEvent")
    suspend fun getAll(): List<SkinTempEntity>
    @Insert
    suspend fun insertSkinTempEvent(skinTempEntity: SkinTempEntity)

    @Insert
    suspend fun insertSkinTempEvents(skinTempEntities: List<SkinTempEntity>)

    @Query("DELETE FROM skinTempEvent")
    suspend fun deleteAll()
}