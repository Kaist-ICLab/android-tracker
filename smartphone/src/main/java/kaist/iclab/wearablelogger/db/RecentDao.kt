package kaist.iclab.wearablelogger.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface RecentDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertEvents(recentEntity: List<RecentEntity>)

    @Query("SELECT * FROM recent ORDER BY timestamp DESC LIMIT 1")
    fun getLastEvent(): Flow<RecentEntity?>
}