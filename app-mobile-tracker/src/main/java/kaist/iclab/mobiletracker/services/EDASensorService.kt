package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.EDASensorData

/**
 * Service for handling EDA (Electrodermal Activity) sensor data operations with Supabase
 */
class EDASensorService()
    : BaseSensorService<EDASensorData>(AppConfig.SupabaseTables.EDA_SENSOR, "EDA") {
    
    override fun prepareData(data: EDASensorData): EDASensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertEDASensorData(data: EDASensorData) {
        insertToSupabase(prepareData(data))
    }
    
    suspend fun insertEDASensorDataBatch(dataList: List<EDASensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

