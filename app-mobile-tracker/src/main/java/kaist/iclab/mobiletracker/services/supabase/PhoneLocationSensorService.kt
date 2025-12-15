package kaist.iclab.mobiletracker.services.supabase

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.sensors.common.LocationSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling phone location sensor data operations with Supabase
 */
class PhoneLocationSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSupabaseService<LocationSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.PHONE_LOCATION_SENSOR,
    sensorName = "Phone Location"
) {
    
    override fun prepareData(data: LocationSensorData): LocationSensorData {
        // Don't override UUID - it should already be set to user UUID from mapper
        return data
    }
    
    suspend fun insertPhoneLocationSensorData(data: LocationSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }
    
    suspend fun insertPhoneLocationSensorDataBatch(dataList: List<LocationSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}
