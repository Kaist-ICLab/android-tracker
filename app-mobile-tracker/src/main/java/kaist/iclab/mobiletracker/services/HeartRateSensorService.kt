package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.data.watch.HeartRateSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling heart rate sensor data operations with Supabase
 */
class HeartRateSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<HeartRateSensorData>(supabaseHelper, "heart_rate_sensor", "heart rate") {
    
    override fun prepareData(data: HeartRateSensorData): HeartRateSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertHeartRateSensorData(data: HeartRateSensorData) = insertSensorData(data)
    fun insertHeartRateSensorDataBatch(dataList: List<HeartRateSensorData>) = insertSensorDataBatch(dataList)
}

