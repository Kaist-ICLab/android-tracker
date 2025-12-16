package kaist.iclab.mobiletracker.repository

import androidx.room.withTransaction
import kaist.iclab.mobiletracker.db.TrackerRoomDB
import kaist.iclab.mobiletracker.db.dao.common.BaseDao
import kaist.iclab.mobiletracker.db.dao.common.LocationDao
import kaist.iclab.mobiletracker.db.entity.watch.WatchAccelerometerEntity
import kaist.iclab.mobiletracker.db.entity.watch.WatchEDAEntity
import kaist.iclab.mobiletracker.db.entity.watch.WatchHeartRateEntity
import kaist.iclab.mobiletracker.db.entity.common.LocationEntity
import kaist.iclab.mobiletracker.db.entity.watch.WatchPPGEntity
import kaist.iclab.mobiletracker.db.entity.watch.WatchSkinTemperatureEntity
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.services.upload.WatchSensorUploadService
import kaist.iclab.mobiletracker.utils.SupabaseSessionHelper

/**
 * Implementation of WatchSensorRepository using Room database.
 * Uses a Map pattern similar to PhoneSensorRepository for consistency.
 * Now uses unified BaseDao interface for both phone and watch sensors.
 * All operations (inserts and queries) go through the Map pattern for full abstraction.
 */
class WatchSensorRepositoryImpl(
    private val db: TrackerRoomDB,
    private val watchSensorDaos: Map<String, BaseDao<*, *>>,
    private val supabaseHelper: SupabaseHelper
) : WatchSensorRepository {
    
    override suspend fun insertHeartRateData(entities: List<WatchHeartRateEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient) ?: ""
                val entitiesWithUuid = entities.map { it.copy(uuid = userUuid) }
                db.withTransaction {
                    @Suppress("UNCHECKED_CAST")
                    val dao = watchSensorDaos[WatchSensorUploadService.HEART_RATE_SENSOR_ID] as? BaseDao<WatchHeartRateEntity, *>
                    if (dao != null) {
                        dao.insertBatch(entitiesWithUuid, userUuid)
                    } else {
                        throw IllegalStateException("No DAO found for Heart Rate sensor")
                    }
                }
            }
        }
    }
    
    override suspend fun insertAccelerometerData(entities: List<WatchAccelerometerEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient) ?: ""
                val entitiesWithUuid = entities.map { it.copy(uuid = userUuid) }
                db.withTransaction {
                    @Suppress("UNCHECKED_CAST")
                    val dao = watchSensorDaos[WatchSensorUploadService.ACCELEROMETER_SENSOR_ID] as? BaseDao<WatchAccelerometerEntity, *>
                    if (dao != null) {
                        dao.insertBatch(entitiesWithUuid, userUuid)
                    } else {
                        throw IllegalStateException("No DAO found for Accelerometer sensor")
                    }
                }
            }
        }
    }
    
    override suspend fun insertEDAData(entities: List<WatchEDAEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient) ?: ""
                val entitiesWithUuid = entities.map { it.copy(uuid = userUuid) }
                db.withTransaction {
                    @Suppress("UNCHECKED_CAST")
                    val dao = watchSensorDaos[WatchSensorUploadService.EDA_SENSOR_ID] as? BaseDao<WatchEDAEntity, *>
                    if (dao != null) {
                        dao.insertBatch(entitiesWithUuid, userUuid)
                    } else {
                        throw IllegalStateException("No DAO found for EDA sensor")
                    }
                }
            }
        }
    }
    
    override suspend fun insertPPGData(entities: List<WatchPPGEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient) ?: ""
                val entitiesWithUuid = entities.map { it.copy(uuid = userUuid) }
                db.withTransaction {
                    @Suppress("UNCHECKED_CAST")
                    val dao = watchSensorDaos[WatchSensorUploadService.PPG_SENSOR_ID] as? BaseDao<WatchPPGEntity, *>
                    if (dao != null) {
                        dao.insertBatch(entitiesWithUuid, userUuid)
                    } else {
                        throw IllegalStateException("No DAO found for PPG sensor")
                    }
                }
            }
        }
    }
    
    override suspend fun insertSkinTemperatureData(entities: List<WatchSkinTemperatureEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient) ?: ""
                val entitiesWithUuid = entities.map { it.copy(uuid = userUuid) }
                db.withTransaction {
                    @Suppress("UNCHECKED_CAST")
                    val dao = watchSensorDaos[WatchSensorUploadService.SKIN_TEMPERATURE_SENSOR_ID] as? BaseDao<WatchSkinTemperatureEntity, *>
                    if (dao != null) {
                        dao.insertBatch(entitiesWithUuid, userUuid)
                    } else {
                        throw IllegalStateException("No DAO found for Skin Temperature sensor")
                    }
                }
            }
        }
    }
    
    override suspend fun insertLocationData(entities: List<LocationEntity>): Result<Unit> {
        return runCatchingSuspend {
            if (entities.isNotEmpty()) {
                val userUuid = SupabaseSessionHelper.getUuidOrNull(supabaseHelper.supabaseClient) ?: ""
                val entitiesWithUuid = entities.map { it.copy(uuid = userUuid) }
                db.withTransaction {
                    val dao = watchSensorDaos[WatchSensorUploadService.LOCATION_SENSOR_ID] as? LocationDao
                    if (dao != null) {
                        dao.insertLocationEntities(entitiesWithUuid)
                    } else {
                        throw IllegalStateException("No DAO found for Location sensor")
                    }
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
    
    override suspend fun flushAllData(): Result<Unit> {
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

