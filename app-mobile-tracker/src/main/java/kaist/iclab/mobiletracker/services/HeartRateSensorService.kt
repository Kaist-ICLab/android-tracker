package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.sensors.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling heart rate sensor data operations with Supabase
 */
class HeartRateSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<HeartRateSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.HEART_RATE_SENSOR,
    sensorName = "heart rate"
) {
    
    override fun prepareData(data: HeartRateSensorData): HeartRateSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertHeartRateSensorData(data: HeartRateSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertHeartRateSensorDataBatch(dataList: List<HeartRateSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

