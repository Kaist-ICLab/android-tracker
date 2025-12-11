package kaist.iclab.mobiletracker.db.dao.common

/**
 * Base interface for all sensor DAOs (both phone and watch sensors).
 * Provides common operations for sensor data storage and retrieval.
 * 
 * @param TEntity The entity type for insert operations (SensorEntity for phone sensors, Room entity for watch sensors)
 * @param TRoom The Room entity type returned by queries (same as TEntity for watch sensors, different for phone sensors)
 */
interface BaseDao<TEntity, TRoom> {
    /**
     * Insert sensor data (single entity)
     */
    suspend fun insert(sensorEntity: TEntity)
    
    /**
     * Insert sensor data in batch (multiple entities)
     * @param entities List of entities to insert
     */
    suspend fun insertBatch(entities: List<TEntity>)
    
    /**
     * Delete all data
     */
    suspend fun deleteAll()
    
    /**
     * Get the latest timestamp from stored data
     */
    suspend fun getLatestTimestamp(): Long?
    
    /**
     * Get the record count
     */
    suspend fun getRecordCount(): Int
    
    /**
     * Get data after a specific timestamp (for deduplication during upload)
     * Returns Room entities for use in upload services.
     * @param afterTimestamp The timestamp threshold (in milliseconds)
     * @return List of Room entities with timestamp greater than the threshold
     */
    suspend fun getDataAfterTimestamp(afterTimestamp: Long): List<TRoom>
}

