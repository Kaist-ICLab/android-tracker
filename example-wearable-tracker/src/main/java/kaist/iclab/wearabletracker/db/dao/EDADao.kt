package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.EDASensor
import kaist.iclab.wearabletracker.db.entity.EDAEntity
import kaist.iclab.wearabletracker.db.summary.EDASummary

@Dao
interface EDADao: BaseDao<EDASensor.Entity, EDASummary> {
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

    @Query("""
        SELECT 
            (timestamp / 600000) * 600000 AS bucketStart,
            COUNT(*) AS count,
            AVG(skinConductance) AS avg,
            AVG(skinConductance * skinConductance) - AVG(skinConductance) * AVG(skinConductance) AS variance,
            SUM(CASE WHEN status != 0 THEN 1 ELSE 0 END) AS badStatusCount
        FROM eda
        WHERE bucketStart BETWEEN :startTime and :endTime
        GROUP BY bucketStart
        ORDER BY bucketStart
    """)
    override suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<EDASummary>
}