package kaist.iclab.mobiletracker.repository

import kaist.iclab.tracker.sensor.core.SensorEntity

/**
 * Repository interface for phone sensor data operations.
 * Provides abstraction for phone sensor data storage and retrieval using Room database.
 */
interface PhoneSensorRepository {
    /**
     * Insert sensor data for a specific sensor
     * @param sensorId The ID of the sensor
     * @param entity The sensor entity to insert
     * @return true if insertion was successful, false otherwise
     */
    suspend fun insertSensorData(sensorId: String, entity: SensorEntity): Boolean
    
    /**
     * Delete all data for a specific sensor
     * @param sensorId The ID of the sensor
     */
    suspend fun deleteAllSensorData(sensorId: String)
    
    /**
     * Check if a sensor has a registered data storage
     * @param sensorId The ID of the sensor
     * @return true if storage is available, false otherwise
     */
    fun hasStorageForSensor(sensorId: String): Boolean
}

