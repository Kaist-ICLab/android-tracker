package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling accelerometer sensor data operations with Supabase
 */
class AccelerometerSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<AccelerometerSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.ACCELEROMETER_SENSOR,
    sensorName = "accelerometer"
) {
    
    override fun prepareData(data: AccelerometerSensorData): AccelerometerSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertAccelerometerSensorData(data: AccelerometerSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertAccelerometerSensorDataBatch(dataList: List<AccelerometerSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

