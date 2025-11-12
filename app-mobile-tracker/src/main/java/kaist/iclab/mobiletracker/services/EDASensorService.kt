package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.data.watch.EDASensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling EDA (Electrodermal Activity) sensor data operations with Supabase
 */
class EDASensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<EDASensorData>(supabaseHelper, "eda_sensor", "EDA") {
    
    override fun prepareData(data: EDASensorData): EDASensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertEDASensorData(data: EDASensorData) = insertSensorData(data)
    fun insertEDASensorDataBatch(dataList: List<EDASensorData>) = insertSensorDataBatch(dataList)
}

