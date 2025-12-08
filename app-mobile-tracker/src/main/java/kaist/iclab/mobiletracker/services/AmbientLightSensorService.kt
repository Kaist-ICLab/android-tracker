package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.sensors.phone.AmbientLightSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling ambient light sensor data operations with Supabase
 */
class AmbientLightSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<AmbientLightSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.AMBIENT_LIGHT_SENSOR,
    sensorName = "Ambient Light"
) {
    
    override fun prepareData(data: AmbientLightSensorData): AmbientLightSensorData {
        // Don't override UUID - it should already be set to user UUID
        // Only clear created_at as it will be set by Supabase
        return data.copy(
            created_at = null
        )
    }
    
    suspend fun insertAmbientLightSensorData(data: AmbientLightSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertAmbientLightSensorDataBatch(dataList: List<AmbientLightSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

