package kaist.iclab.wearablelogger.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PpgDao {
    @Query("SELECT * FROM ppgEvent")
    fun getAll(): List<PpgEntity>
//    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Insert
    suspend fun insertPpgEvent(ppgEntity: PpgEntity)

    @Query("DELETE FROM ppgEvent")
    suspend fun deleteAll()
}