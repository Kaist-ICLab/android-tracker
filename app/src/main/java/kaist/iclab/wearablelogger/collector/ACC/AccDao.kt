package kaist.iclab.wearablelogger.collector.ACC

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccDao {
    @Query("SELECT * FROM accEvent")
    suspend fun getAll(): List<AccEntity>

    @Insert
    suspend fun insertAccEvent(accEntity: AccEntity)

    @Insert
    suspend fun insertAccEvents(accEntities: List<AccEntity>)

    @Query("DELETE FROM accEvent")
    suspend fun deleteAll()
}