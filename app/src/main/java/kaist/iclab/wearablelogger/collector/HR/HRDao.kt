package kaist.iclab.wearablelogger.collector.HR

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HRDao {
    @Query("SELECT * FROM hrEvent")
    fun getAll(): List<HREntity>
    @Insert
    suspend fun insertHREvent(hrEntity: HREntity)
    @Insert
    suspend fun insertHREvents(hrEntities: List<HREntity>)
    @Query("DELETE FROM hrEvent")
    suspend fun deleteAll()
}