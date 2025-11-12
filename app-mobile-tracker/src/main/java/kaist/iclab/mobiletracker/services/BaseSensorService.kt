package kaist.iclab.mobiletracker.services

import android.util.Log
import io.github.jan.supabase.postgrest.from
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Base service class for handling sensor data operations with Supabase.
 * Provides common functionality for inserting sensor data.
 * 
 * @param T The sensor data type (must have uuid and created_at fields that can be set via copy())
 * @param tableName The Supabase table name
 * @param sensorName The sensor name for logging purposes
 */
abstract class BaseSensorService<T>(
    protected val supabaseHelper: SupabaseHelper = SupabaseHelper(),
    private val tableName: String,
    private val sensorName: String
) {
    protected val supabaseClient = supabaseHelper.supabaseClient

    /**
     * Get UUID for sensor data entries.
     * TODO: Replace with dynamic UUID generation (UUID.randomUUID().toString()) for production
     * Currently using hardcoded UUID from AppConfig for testing purposes
     */
    protected fun getSensorDataUuid(): String {
        return AppConfig.SENSOR_DATA_UUID
    }

    /**
     * Prepare data for insertion by adding UUID and clearing created_at.
     * Each child class implements this to call the appropriate copy() method.
     */
    protected abstract fun prepareData(data: T): T

    /**
     * Insert single sensor data entry into Supabase
     */
    fun insertSensorData(data: T) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preparedData = prepareData(data)
                supabaseClient.from(tableName).insert(preparedData)
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "$sensorName sensor data inserted successfully")
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_SUPABASE, "Error inserting $sensorName sensor data: ${e.message}", e)
            }
        }
    }

    /**
     * Insert multiple sensor data entries into Supabase
     */
    fun insertSensorDataBatch(dataList: List<T>) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                if (dataList.isEmpty()) {
                    Log.w(AppConfig.LogTags.PHONE_SUPABASE, "Empty $sensorName data list, skipping insert")
                    return@launch
                }
                
                val preparedDataList = dataList.map { prepareData(it) }
                supabaseClient.from(tableName).insert(preparedDataList)
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Inserted ${preparedDataList.size} $sensorName sensor data entries")
            } catch (e: Exception) {
                Log.e(AppConfig.LogTags.PHONE_SUPABASE, "Error inserting $sensorName sensor data batch: ${e.message}", e)
            }
        }
    }
}

