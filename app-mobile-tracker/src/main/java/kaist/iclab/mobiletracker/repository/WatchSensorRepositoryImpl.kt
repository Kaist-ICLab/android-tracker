package kaist.iclab.mobiletracker.repository

import androidx.room.withTransaction
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.dao.BaseDao
import kaist.iclab.mobiletracker.db.entity.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.WatchLocationEntity
import kaist.iclab.mobiletracker.db.entity.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.WatchSkinTemperatureEntity
import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService

/**
 * Implementation of WatchSensorRepository using Room database.
 * Uses a Map pattern similar to PhoneSensorRepository for consistency.
 * Now uses unified BaseDao interface for both phone and watch sensors.
 */
class WatchSensorRepositoryImpl(
    private val db: TrackerRoomDB,
    private val watchSensorDaos: Map<String, BaseDao<*, *>>
) : WatchSensorRepository {
    
    override suspend fun insertHeartRateData(entities: List<WatchHeartRateEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                db.withTransaction {
                    db.watchHeartRateDao().insert(entities)
                }
            }
        }
    }
    
    override suspend fun insertAccelerometerData(entities: List<WatchAccelerometerEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                db.withTransaction {
                    db.watchAccelerometerDao().insert(entities)
                }
            }
        }
    }
    
    override suspend fun insertEDAData(entities: List<WatchEDAEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                db.withTransaction {
                    db.watchEDADao().insert(entities)
                }
            }
        }
    }
    
    override suspend fun insertPPGData(entities: List<WatchPPGEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                db.withTransaction {
                    db.watchPPGDao().insert(entities)
                }
            }
        }
    }
    
    override suspend fun insertSkinTemperatureData(entities: List<WatchSkinTemperatureEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                db.withTransaction {
                    db.watchSkinTemperatureDao().insert(entities)
                }
            }
        }
    }
    
    override suspend fun insertLocationData(entities: List<WatchLocationEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                db.withTransaction {
                    db.watchLocationDao().insert(entities)
                }
            }
        }
    }
    
    override suspend fun getLatestTimestamp(sensorId: String): Long? {
        return runCatchingSuspend {
            @Suppress("UNCHECKED_CAST")
            val dao = watchSensorDaos[sensorId] as? BaseDao<*, *>
            dao?.getLatestTimestamp()
        }.getOrNull()
    }
    
    override suspend fun getRecordCount(sensorId: String): Int {
        return runCatchingSuspend {
            @Suppress("UNCHECKED_CAST")
            val dao = watchSensorDaos[sensorId] as? BaseDao<*, *>
            dao?.getRecordCount() ?: 0
        }.getOrNull() ?: 0
    }
    
    override suspend fun deleteAllSensorData(sensorId: String): Result<Unit> {
        return runCatchingSuspend {
            @Suppress("UNCHECKED_CAST")
            val dao = watchSensorDaos[sensorId] as? BaseDao<*, *>
            if (dao != null) {
                dao.deleteAll()
            } else {
                throw IllegalArgumentException("Unknown sensor ID: $sensorId")
            }
        }
    }
    
    override suspend fun deleteAllWatchSensorData(): Result<Unit> {
        return runCatchingSuspend {
            db.withTransaction {
                watchSensorDaos.values.forEach { dao ->
                    @Suppress("UNCHECKED_CAST")
                    (dao as? BaseDao<*, *>)?.deleteAll()
                }
            }
        }
    }
}

