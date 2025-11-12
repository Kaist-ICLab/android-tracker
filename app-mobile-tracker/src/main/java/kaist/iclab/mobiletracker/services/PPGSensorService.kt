package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.PPGSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling PPG (Photoplethysmography) sensor data operations with Supabase
 */
class PPGSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<PPGSensorData>(supabaseHelper, AppConfig.SupabaseTables.PPG_SENSOR, "PPG") {
    
    override fun prepareData(data: PPGSensorData): PPGSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertPPGSensorData(data: PPGSensorData) {
        insertToSupabase(prepareData(data))
    }
    
    fun insertPPGSensorDataBatch(dataList: List<PPGSensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

