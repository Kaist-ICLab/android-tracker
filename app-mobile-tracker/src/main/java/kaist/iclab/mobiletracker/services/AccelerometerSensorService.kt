package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.AccelerometerSensorData

/**
 * Service for handling accelerometer sensor data operations with Supabase
 */
class AccelerometerSensorService()
    : BaseSensorService<AccelerometerSensorData>(AppConfig.SupabaseTables.ACCELEROMETER_SENSOR, "accelerometer") {
    
    override fun prepareData(data: AccelerometerSensorData): AccelerometerSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertAccelerometerSensorData(data: AccelerometerSensorData) {
        insertToSupabase(prepareData(data))
    }
    
    suspend fun insertAccelerometerSensorDataBatch(dataList: List<AccelerometerSensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

