package kaist.iclab.mobiletracker.repository

import kaist.iclab.mobiletracker.data.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.data.watch.EDASensorData
import kaist.iclab.mobiletracker.data.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.data.watch.LocationSensorData
import kaist.iclab.mobiletracker.data.watch.PPGSensorData
import kaist.iclab.mobiletracker.data.watch.SkinTemperatureSensorData

/**
 * Repository interface for sensor data operations.
 * Provides abstraction for sensor data storage and retrieval.
 */
interface SensorDataRepository {
    /**
     * Insert location sensor data
     */
    suspend fun insertLocationData(data: LocationSensorData)
    
    /**
     * Insert location sensor data batch
     */
    suspend fun insertLocationDataBatch(dataList: List<LocationSensorData>)
    
    /**
     * Insert accelerometer sensor data
     */
    suspend fun insertAccelerometerData(data: AccelerometerSensorData)
    
    /**
     * Insert accelerometer sensor data batch
     */
    suspend fun insertAccelerometerDataBatch(dataList: List<AccelerometerSensorData>)
    
    /**
     * Insert EDA sensor data
     */
    suspend fun insertEDAData(data: EDASensorData)
    
    /**
     * Insert EDA sensor data batch
     */
    suspend fun insertEDADataBatch(dataList: List<EDASensorData>)
    
    /**
     * Insert heart rate sensor data
     */
    suspend fun insertHeartRateData(data: HeartRateSensorData)
    
    /**
     * Insert heart rate sensor data batch
     */
    suspend fun insertHeartRateDataBatch(dataList: List<HeartRateSensorData>)
    
    /**
     * Insert PPG sensor data
     */
    suspend fun insertPPGData(data: PPGSensorData)
    
    /**
     * Insert PPG sensor data batch
     */
    suspend fun insertPPGDataBatch(dataList: List<PPGSensorData>)
    
    /**
     * Insert skin temperature sensor data
     */
    suspend fun insertSkinTemperatureData(data: SkinTemperatureSensorData)
    
    /**
     * Insert skin temperature sensor data batch
     */
    suspend fun insertSkinTemperatureDataBatch(dataList: List<SkinTemperatureSensorData>)
}

