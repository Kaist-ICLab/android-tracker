package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.watch.AccelerometerSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper

/**
 * Service for handling accelerometer sensor data operations with Supabase
 */
class AccelerometerSensorService(supabaseHelper: SupabaseHelper = SupabaseHelper())
    : BaseSensorService<AccelerometerSensorData>(supabaseHelper, AppConfig.SupabaseTables.ACCELEROMETER_SENSOR, "accelerometer") {
    
    override fun prepareData(data: AccelerometerSensorData): AccelerometerSensorData {
        return data.copy(
            uuid = getSensorDataUuid(),
            created_at = null
        )
    }
    
    fun insertAccelerometerSensorData(data: AccelerometerSensorData) {
        insertToSupabase(prepareData(data))
    }
    
    fun insertAccelerometerSensorDataBatch(dataList: List<AccelerometerSensorData>) {
        val preparedList = dataList.map { prepareData(it) }
        insertBatchToSupabase(preparedList)
    }
}

