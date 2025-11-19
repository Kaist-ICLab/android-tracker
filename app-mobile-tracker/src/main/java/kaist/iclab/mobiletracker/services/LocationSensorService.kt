package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.LocationSensorData

/**
 * Service for handling location sensor data operations with Supabase
 */
class LocationSensorService() 
    : BaseSensorService<LocationSensorData>(AppConfig.SupabaseTables.LOCATION_SENSOR, "location") {
    
    override fun prepareData(data: LocationSensorData): LocationSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertLocationSensorData(data: LocationSensorData) {
        insertToSupabase(prepareData(data))
    }
    
    fun insertLocationSensorDataBatch(dataList: List<LocationSensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

