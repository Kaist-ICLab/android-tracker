package kaist.iclab.mobiletracker.repository

import kaist.iclab.mobiletracker.data.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.data.watch.EDASensorData
import kaist.iclab.mobiletracker.data.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.data.watch.LocationSensorData
import kaist.iclab.mobiletracker.data.watch.PPGSensorData
import kaist.iclab.mobiletracker.data.watch.SkinTemperatureSensorData
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService

/**
 * Implementation of SensorDataRepository using sensor services.
 * Delegates to individual sensor services for data operations.
 */
class SensorDataRepositoryImpl(
    private val locationSensorService: LocationSensorService,
    private val accelerometerSensorService: AccelerometerSensorService,
    private val edaSensorService: EDASensorService,
    private val heartRateSensorService: HeartRateSensorService,
    private val ppgSensorService: PPGSensorService,
    private val skinTemperatureSensorService: SkinTemperatureSensorService
) : SensorDataRepository {
    
    override suspend fun insertLocationData(data: LocationSensorData) {
        locationSensorService.insertLocationSensorData(data)
    }
    
    override suspend fun insertLocationDataBatch(dataList: List<LocationSensorData>) {
        locationSensorService.insertLocationSensorDataBatch(dataList)
    }
    
    override suspend fun insertAccelerometerData(data: AccelerometerSensorData) {
        accelerometerSensorService.insertAccelerometerSensorData(data)
    }
    
    override suspend fun insertAccelerometerDataBatch(dataList: List<AccelerometerSensorData>) {
        accelerometerSensorService.insertAccelerometerSensorDataBatch(dataList)
    }
    
    override suspend fun insertEDAData(data: EDASensorData) {
        edaSensorService.insertEDASensorData(data)
    }
    
    override suspend fun insertEDADataBatch(dataList: List<EDASensorData>) {
        edaSensorService.insertEDASensorDataBatch(dataList)
    }
    
    override suspend fun insertHeartRateData(data: HeartRateSensorData) {
        heartRateSensorService.insertHeartRateSensorData(data)
    }
    
    override suspend fun insertHeartRateDataBatch(dataList: List<HeartRateSensorData>) {
        heartRateSensorService.insertHeartRateSensorDataBatch(dataList)
    }
    
    override suspend fun insertPPGData(data: PPGSensorData) {
        ppgSensorService.insertPPGSensorData(data)
    }
    
    override suspend fun insertPPGDataBatch(dataList: List<PPGSensorData>) {
        ppgSensorService.insertPPGSensorDataBatch(dataList)
    }
    
    override suspend fun insertSkinTemperatureData(data: SkinTemperatureSensorData) {
        skinTemperatureSensorService.insertSkinTemperatureSensorData(data)
    }
    
    override suspend fun insertSkinTemperatureDataBatch(dataList: List<SkinTemperatureSensorData>) {
        skinTemperatureSensorService.insertSkinTemperatureSensorDataBatch(dataList)
    }
}

