package kaist.iclab.wearablelogger.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HRIBIDao {
    @Query("SELECT * FROM HRIBIEvent")
    fun getAll(): List<HRIBIEntity>
//    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Insert
    suspend fun insertHRIBIEvent(hribiEntity: HRIBIEntity)

    @Query("DELETE FROM hribiEvent")
    suspend fun deleteAll()
}