package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling heart rate sensor data operations with Supabase
 */
class HeartRateSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<HeartRateSensorData>(supabaseHelper, AppConfig.SupabaseTables.HEART_RATE_SENSOR, "heart rate") {
    
    override fun prepareData(data: HeartRateSensorData): HeartRateSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertHeartRateSensorData(data: HeartRateSensorData) {
        insertToSupabase(prepareData(data))
    }
    
    fun insertHeartRateSensorDataBatch(dataList: List<HeartRateSensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

