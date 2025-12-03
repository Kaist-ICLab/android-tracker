package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.LocationSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling location sensor data operations with Supabase
 */
class LocationSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<LocationSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.LOCATION_SENSOR,
    sensorName = "location"
) {
    
    override fun prepareData(data: LocationSensorData): LocationSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    suspend fun insertLocationSensorData(data: LocationSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertLocationSensorDataBatch(dataList: List<LocationSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

