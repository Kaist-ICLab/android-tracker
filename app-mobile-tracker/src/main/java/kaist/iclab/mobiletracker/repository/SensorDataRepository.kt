package kaist.iclab.mobiletracker.repository

import kaist.iclab.mobiletracker.data.sensors.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.EDASensorData
import kaist.iclab.mobiletracker.data.sensors.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.PPGSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.SkinTemperatureSensorData

/**
 * Generic repository interface for sensor data operations.
 * Provides abstraction for sensor data storage and retrieval with type-safe error handling.
 * 
 * Uses Result types for explicit error handling and a generic pattern to reduce code duplication.
 */
interface SensorDataRepository {
    /**
     * Insert a single sensor data entry.
     * Generic method that works with any sensor data type.
     * 
     * @param data The sensor data to insert
     * @return Result indicating success or failure with error details
     */
    suspend fun <T : Any> insertData(data: T): Result<Unit>
    
    /**
     * Insert multiple sensor data entries in a batch.
     * Generic method that works with any sensor data type.
     * 
     * @param dataList The list of sensor data to insert
     * @return Result indicating success or failure with error details
     */
    suspend fun <T : Any> insertDataBatch(dataList: List<T>): Result<Unit>
    
    // Type-specific convenience methods for backward compatibility and type safety
    // These delegate to the generic methods above
    
    /**
     * Insert location sensor data
     * @return Result indicating success or failure with error details
     */
    suspend fun insertLocationData(data: LocationSensorData): Result<Unit> = insertData(data)
    
    /**
     * Insert location sensor data batch
     * @return Result indicating success or failure with error details
     */
    suspend fun insertLocationDataBatch(dataList: List<LocationSensorData>): Result<Unit> = insertDataBatch(dataList)
    
    /**
     * Insert accelerometer sensor data
     * @return Result indicating success or failure with error details
     */
    suspend fun insertAccelerometerData(data: AccelerometerSensorData): Result<Unit> = insertData(data)
    
    /**
     * Insert accelerometer sensor data batch
     * @return Result indicating success or failure with error details
     */
    suspend fun insertAccelerometerDataBatch(dataList: List<AccelerometerSensorData>): Result<Unit> = insertDataBatch(dataList)
    
    /**
     * Insert EDA sensor data
     * @return Result indicating success or failure with error details
     */
    suspend fun insertEDAData(data: EDASensorData): Result<Unit> = insertData(data)
    
    /**
     * Insert EDA sensor data batch
     * @return Result indicating success or failure with error details
     */
    suspend fun insertEDADataBatch(dataList: List<EDASensorData>): Result<Unit> = insertDataBatch(dataList)
    
    /**
     * Insert heart rate sensor data
     * @return Result indicating success or failure with error details
     */
    suspend fun insertHeartRateData(data: HeartRateSensorData): Result<Unit> = insertData(data)
    
    /**
     * Insert heart rate sensor data batch
     * @return Result indicating success or failure with error details
     */
    suspend fun insertHeartRateDataBatch(dataList: List<HeartRateSensorData>): Result<Unit> = insertDataBatch(dataList)
    
    /**
     * Insert PPG sensor data
     * @return Result indicating success or failure with error details
     */
    suspend fun insertPPGData(data: PPGSensorData): Result<Unit> = insertData(data)
    
    /**
     * Insert PPG sensor data batch
     * @return Result indicating success or failure with error details
     */
    suspend fun insertPPGDataBatch(dataList: List<PPGSensorData>): Result<Unit> = insertDataBatch(dataList)
    
    /**
     * Insert skin temperature sensor data
     * @return Result indicating success or failure with error details
     */
    suspend fun insertSkinTemperatureData(data: SkinTemperatureSensorData): Result<Unit> = insertData(data)
    
    /**
     * Insert skin temperature sensor data batch
     * @return Result indicating success or failure with error details
     */
    suspend fun insertSkinTemperatureDataBatch(dataList: List<SkinTemperatureSensorData>): Result<Unit> = insertDataBatch(dataList)
}

