package kaist.iclab.wearablelogger.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SkinTempDao {
    @Query("SELECT * FROM skinTempEvent")
    fun getAll(): List<SkinTempEntity>
    @Insert
    suspend fun insertSkinTempEvent(skinTempEntity: SkinTempEntity)

    @Query("DELETE FROM skinTempEvent")
    suspend fun deleteAll()
}