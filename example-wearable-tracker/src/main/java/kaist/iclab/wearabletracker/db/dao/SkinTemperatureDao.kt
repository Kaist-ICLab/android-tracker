package kaist.iclab.wearabletracker.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.loggerstructure.summary.SkinTemperatureSummary
import kaist.iclab.tracker.sensor.galaxywatch.SkinTemperatureSensor
import kaist.iclab.wearabletracker.db.entity.HeartRateEntity
import kaist.iclab.wearabletracker.db.entity.SkinTemperatureEntity

@Dao
interface SkinTemperatureDao: BaseDao<SkinTemperatureSensor.Entity, SkinTemperatureSummary> {
    override suspend fun insert(sensorEntity: SkinTemperatureSensor.Entity) {
        val entity = sensorEntity.dataPoint.map {
            SkinTemperatureEntity(
                received = it.received,
                timestamp = it.timestamp,
                objectTemperature = it.objectTemperature,
                ambientTemperature = it.ambientTemperature,
                status = it.status
            )
        }
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(skinTemperatureEntity: List<SkinTemperatureEntity>)

    @Query("""
        SELECT 
            (timestamp / 600000) * 600000 AS bucketStart,
            COUNT(*) AS count,
            AVG(objectTemperature) AS avgObjectTemp,
            AVG(ambientTemperature) AS avgAmbientTemp,
            AVG(objectTemperature * objectTemperature) - AVG(objectTemperature) * AVG(objectTemperature) AS varObjectTemp,
            SUM(CASE WHEN status != 0 THEN 1 ELSE 0 END) AS badStatusCount
        FROM skin_temperature
        WHERE bucketStart BETWEEN :startTime AND :endTime
        GROUP BY bucketStart
        ORDER BY bucketStart
    """)
    override suspend fun getSummaryBetween(startTime: Long, endTime: Long): List<SkinTemperatureSummary>
}