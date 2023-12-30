package kaist.iclab.wearablelogger.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AccDao {
    @Query("SELECT * FROM accEvent")
    fun getAll(): List<AccEntity>
//    @Insert(onConflict = OnConflictStrategy.ABORT)
    @Insert
    suspend fun insertAccEvent(accEntity: AccEntity)

    @Query("DELETE FROM accEvent")
    suspend fun deleteAll()
}