package kaist.iclab.mobiletracker.db.dao.phone

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.entity.phone.ConnectivityEntity
import kaist.iclab.tracker.sensor.phone.ConnectivitySensor

@Dao
interface ConnectivityDao : BaseDao<ConnectivitySensor.Entity, ConnectivityEntity> {
    override suspend fun insert(sensorEntity: ConnectivitySensor.Entity, userUuid: String?) {
        val entity = ConnectivityEntity(
            uuid = userUuid ?: "",
            received = sensorEntity.received,
            timestamp = sensorEntity.timestamp,
            networkType = sensorEntity.networkType,
            isConnected = sensorEntity.isConnected,
            hasInternet = sensorEntity.hasInternet,
            transportTypes = sensorEntity.transportTypes.joinToString(",")
        )
        insertUsingRoomEntity(entity)
    }

    @Insert
    suspend fun insertUsingRoomEntity(connectivityEntity: ConnectivityEntity)

    @Insert
    suspend fun insertBatchUsingRoomEntity(entities: List<ConnectivityEntity>)

    override suspend fun insertBatch(entities: List<ConnectivitySensor.Entity>, userUuid: String?) {
        val roomEntities = entities.map { entity ->
            ConnectivityEntity(
                uuid = userUuid ?: "",
                received = entity.received,
                timestamp = entity.timestamp,
                networkType = entity.networkType,
                isConnected = entity.isConnected,
                hasInternet = entity.hasInternet,
                transportTypes = entity.transportTypes.joinToString(",")
            )
        }
        insertBatchUsingRoomEntity(roomEntities)
    }

    @Query("SELECT * FROM ConnectivityEntity ORDER BY timestamp ASC")
    suspend fun getAllConnectivityData(): List<ConnectivityEntity>

    @Query("SELECT * FROM ConnectivityEntity WHERE timestamp > :afterTimestamp ORDER BY timestamp ASC")
    override suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<ConnectivityEntity>

    @Query("SELECT MAX(timestamp) FROM ConnectivityEntity")
    override suspend fun getLatestTimestamp(): Long?

    @Query("SELECT COUNT(*) FROM ConnectivityEntity")
    override suspend fun getRecordCount(): Int

    @Query("DELETE FROM ConnectivityEntity")
    suspend fun deleteAllConnectivityData()

    override suspend fun deleteAll() {
        deleteAllConnectivityData()
    }
}

