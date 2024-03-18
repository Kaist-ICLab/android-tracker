package kaist.iclab.wearablelogger.collector.HR

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.wearablelogger.collector.ACC.AccEntity

@Dao
interface HRDao {
    @Query("SELECT * FROM hrEvent")
    suspend fun getAll(): List<HREntity>
    @Insert
    suspend fun insertHREvent(hrEntity: HREntity)
    @Insert
    suspend fun insertHREvents(hrEntities: List<HREntity>)
    @Query("DELETE FROM hrEvent")
    suspend fun deleteAll()

    @Query("SELECT * FROM hrEvent ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLast(): HREntity
}