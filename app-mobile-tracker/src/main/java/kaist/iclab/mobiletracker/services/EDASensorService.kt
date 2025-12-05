package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.sensors.watch.EDASensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling EDA (Electrodermal Activity) sensor data operations with Supabase
 */
class EDASensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<EDASensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.EDA_SENSOR,
    sensorName = "EDA"
) {
    
    override fun prepareData(data: EDASensorData): EDASensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertEDASensorData(data: EDASensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertEDASensorDataBatch(dataList: List<EDASensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

