package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.SkinTemperatureSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling skin temperature sensor data operations with Supabase
 */
class SkinTemperatureSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<SkinTemperatureSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.SKIN_TEMPERATURE_SENSOR,
    sensorName = "skin temperature"
) {
    
    override fun prepareData(data: SkinTemperatureSensorData): SkinTemperatureSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertSkinTemperatureSensorData(data: SkinTemperatureSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertSkinTemperatureSensorDataBatch(dataList: List<SkinTemperatureSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

