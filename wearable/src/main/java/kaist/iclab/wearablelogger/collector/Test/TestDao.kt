package kaist.iclab.wearablelogger.collector.Test

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kaist.iclab.wearablelogger.collector.PPGGreen.PpgEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertTestEvent(testEntity: TestEntity)

    @Query("SELECT * FROM testEvent WHERE timestamp > :timestamp")
    fun queryTestEvent(timestamp: Long): Flow<List<TestEntity>>

    @Query("SELECT * FROM testEvent")
    suspend fun getAll(): List<TestEntity>
}