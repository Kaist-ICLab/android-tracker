package kaist.iclab.mobiletracker.services

import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.sensors.phone.WifiSensorData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result

/**
 * Service for handling WiFi sensor data operations with Supabase
 */
class WifiSensorService(
    supabaseHelper: SupabaseHelper
) : BaseSensorService<WifiSensorData>(
    supabaseHelper = supabaseHelper,
    tableName = AppConfig.SupabaseTables.WIFI_SENSOR,
    sensorName = "WiFi"
) {

    override fun prepareData(data: WifiSensorData): WifiSensorData {
        // Don't override UUID - it should already be set to user UUID
        return data
    }

    suspend fun insertWifiSensorData(data: WifiSensorData): Result<Unit> {
        return insertToSupabase(prepareData(data))
    }

    suspend fun insertWifiSensorDataBatch(dataList: List<WifiSensorData>): Result<Unit> {
        val preparedList = dataList.map { prepareData(it) }
        return insertBatchToSupabase(preparedList)
    }
}

