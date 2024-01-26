package kaist.iclab.wearablelogger.collector.PPGGreen

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PpgDao {
    @Query("SELECT * FROM ppgEvent")
    suspend fun getAll(): List<PpgEntity>
    @Insert
    suspend fun insertPpgEvent(ppgEntity: PpgEntity)

    @Insert
    suspend fun insertPpgEvents(ppgEntities: List<PpgEntity>)

    @Query("DELETE FROM ppgEvent")
    suspend fun deleteAll()
}