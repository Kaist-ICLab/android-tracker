package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.data.watch.SkinTemperatureSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling skin temperature sensor data operations with Supabase
 */
class SkinTemperatureSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<SkinTemperatureSensorData>(supabaseHelper, "skin_temperature_sensor", "skin temperature") {
    
    override fun prepareData(data: SkinTemperatureSensorData): SkinTemperatureSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertSkinTemperatureSensorData(data: SkinTemperatureSensorData) = insertSensorData(data)
    fun insertSkinTemperatureSensorDataBatch(dataList: List<SkinTemperatureSensorData>) = insertSensorDataBatch(dataList)
}

