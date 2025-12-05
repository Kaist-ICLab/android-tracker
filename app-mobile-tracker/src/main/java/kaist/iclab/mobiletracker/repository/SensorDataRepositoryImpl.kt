package kaist.iclab.mobiletracker.repository

import android.util.Log
import kaist.iclab.mobiletracker.data.sensors.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.EDASensorData
import kaist.iclab.mobiletracker.data.sensors.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.LocationSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.PPGSensorData
import kaist.iclab.mobiletracker.data.sensors.watch.SkinTemperatureSensorData
import kaist.iclab.mobiletracker.services.AccelerometerSensorService
import kaist.iclab.mobiletracker.services.EDASensorService
import kaist.iclab.mobiletracker.services.HeartRateSensorService
import kaist.iclab.mobiletracker.services.LocationSensorService
import kaist.iclab.mobiletracker.services.PPGSensorService
import kaist.iclab.mobiletracker.services.SkinTemperatureSensorService

/**
 * Implementation of SensorDataRepository using sensor services.
 * Uses a generic pattern to route data to the appropriate service based on type.
 */
class SensorDataRepositoryImpl(
    private val locationSensorService: LocationSensorService,
    private val accelerometerSensorService: AccelerometerSensorService,
    private val edaSensorService: EDASensorService,
    private val heartRateSensorService: HeartRateSensorService,
    private val ppgSensorService: PPGSensorService,
    private val skinTemperatureSensorService: SkinTemperatureSensorService
) : SensorDataRepository {
    
    companion object {
        private const val TAG = "SensorDataRepository"
    }
    
    /**
     * Generic implementation that routes data to the appropriate service based on type.
     */
    override suspend fun <T : Any> insertData(data: T): Result<Unit> {
        return when (data) {
            is LocationSensorData -> locationSensorService.insertLocationSensorData(data)
            is AccelerometerSensorData -> accelerometerSensorService.insertAccelerometerSensorData(data)
            is EDASensorData -> edaSensorService.insertEDASensorData(data)
            is HeartRateSensorData -> heartRateSensorService.insertHeartRateSensorData(data)
            is PPGSensorData -> ppgSensorService.insertPPGSensorData(data)
            is SkinTemperatureSensorData -> skinTemperatureSensorService.insertSkinTemperatureSensorData(data)
            else -> {
                val error = IllegalArgumentException("Unsupported sensor data type: ${data::class.simpleName}")
                Log.e(TAG, error.message ?: "Unknown error")
                Result.Error(error)
            }
        }
    }
    
    /**
     * Generic batch implementation that routes data to the appropriate service based on type.
     */
    override suspend fun <T : Any> insertDataBatch(dataList: List<T>): Result<Unit> {
        if (dataList.isEmpty()) {
            return Result.Success(Unit)
        }
        
        return when (val firstItem = dataList.first()) {
            is LocationSensorData -> {
                @Suppress("UNCHECKED_CAST")
                locationSensorService.insertLocationSensorDataBatch(dataList as List<LocationSensorData>)
            }
            is AccelerometerSensorData -> {
                @Suppress("UNCHECKED_CAST")
                accelerometerSensorService.insertAccelerometerSensorDataBatch(dataList as List<AccelerometerSensorData>)
            }
            is EDASensorData -> {
                @Suppress("UNCHECKED_CAST")
                edaSensorService.insertEDASensorDataBatch(dataList as List<EDASensorData>)
            }
            is HeartRateSensorData -> {
                @Suppress("UNCHECKED_CAST")
                heartRateSensorService.insertHeartRateSensorDataBatch(dataList as List<HeartRateSensorData>)
            }
            is PPGSensorData -> {
                @Suppress("UNCHECKED_CAST")
                ppgSensorService.insertPPGSensorDataBatch(dataList as List<PPGSensorData>)
            }
            is SkinTemperatureSensorData -> {
                @Suppress("UNCHECKED_CAST")
                skinTemperatureSensorService.insertSkinTemperatureSensorDataBatch(dataList as List<SkinTemperatureSensorData>)
            }
            else -> {
                val error = IllegalArgumentException("Unsupported sensor data type: ${firstItem::class.simpleName}")
                Log.e(TAG, error.message ?: "Unknown error")
                Result.Error(error)
            }
        }
    }
}

