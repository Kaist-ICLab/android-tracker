package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.HeartRateSensorData

/**
 * Service for handling heart rate sensor data operations with Supabase
 */
class HeartRateSensorService()
    : BaseSensorService<HeartRateSensorData>(AppConfig.SupabaseTables.HEART_RATE_SENSOR, "heart rate") {
    
    override fun prepareData(data: HeartRateSensorData): HeartRateSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertHeartRateSensorData(data: HeartRateSensorData) {
        insertToSupabase(prepareData(data))
    }
    
    suspend fun insertHeartRateSensorDataBatch(dataList: List<HeartRateSensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

