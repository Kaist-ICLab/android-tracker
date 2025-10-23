package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.tracker.sensor.galaxywatch.PPGSensor
import kaist.iclab.wearabletracker.db.entity.PPGEntity
import kaist.iclab.wearabletracker.db.summary.PPGSummary

@Dao
interface PPGDao: BaseDao<PPGSensor.Entity, PPGSummary> {
    override suspend fun insert(sensorEntity: PPGSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            PPGEntity(
                received = it.received,
                timestamp = it.timestamp,
                green = it.green,
                red = it.red,
                ir = it.ir,
                greenStatus = it.greenStatus,
                redStatus = it.redStatus,
                irStatus = it.irStatus,
            )
        }

        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(ppgEntity: List<PPGEntity>)

    @Query("""
        SELECT 
            (timestamp / 600000) * 600000 AS bucketStart,
            COUNT(*) AS count,
            AVG(green) AS avgPpgGreen,
            AVG(ir) AS avgPpgIR,
            AVG(red) AS avgPpgRed,
            AVG(red * red) - AVG(red) * AVG(red) AS varPpgRed,
            SUM(CASE WHEN redStatus != 0 THEN 1 ELSE 0 END) AS badStatusCount
        FROM ppg
        WHERE bucketStart BETWEEN :startTime and :endTime
        GROUP BY bucketStart
        ORDER BY bucketStart
    """)
    override suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<PPGSummary>
}