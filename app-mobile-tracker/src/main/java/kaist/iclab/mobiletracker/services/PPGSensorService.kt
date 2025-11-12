package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.data.watch.PPGSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling PPG (Photoplethysmography) sensor data operations with Supabase
 */
class PPGSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<PPGSensorData>(supabaseHelper, "ppg_sensor", "PPG") {
    
    override fun prepareData(data: PPGSensorData): PPGSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertPPGSensorData(data: PPGSensorData) = insertSensorData(data)
    fun insertPPGSensorDataBatch(dataList: List<PPGSensorData>) = insertSensorDataBatch(dataList)
}

