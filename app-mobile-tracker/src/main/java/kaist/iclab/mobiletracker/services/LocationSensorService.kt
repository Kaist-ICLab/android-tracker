package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.data.watch.LocationSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling location sensor data operations with Supabase
 */
class LocationSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper()) 
    : BaseSensorService<LocationSensorData>(supabaseHelper, "location_sensor", "location") {
    
    override fun prepareData(data: LocationSensorData): LocationSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertLocationSensorData(data: LocationSensorData) = insertSensorData(data)
    fun insertLocationSensorDataBatch(dataList: List<LocationSensorData>) = insertSensorDataBatch(dataList)
}

