package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.EDASensor
import kaist.iclab.wearabletracker.db.entity.CsvSerializable
import kaist.iclab.wearabletracker.db.entity.EDAEntity

@Dao
interface EDADao: BaseDao<EDASensor.Entity> {
    override suspend fun insert(sensorEntity: EDASensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            EDAEntity(
                received = it.received,
                timestamp = it.timestamp,
                skinConductance = it.skinConductance,
                status = it.status
            )
        }

        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(edaEntity: List<EDAEntity>)

    @Query("SELECT * FROM EDAEntity ORDER BY timestamp ASC")
    suspend fun getAllEDAData(): List<EDAEntity>

    override suspend fun getAllForExport(): List<CsvSerializable> = getAllEDAData()

    @Query("DELETE FROM EDAEntity")
    suspend fun deleteAllEDAData()

    override suspend fun deleteAll() {
        deleteAllEDAData()
    }
}