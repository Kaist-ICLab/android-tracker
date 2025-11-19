package kaist.iclab.mobiletracker.services

import android.util.Log
import io.github.jan.supabase.postgrest.from
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kotlinx.serialization.Serializable

/**
 * Base service class for handling sensor data operations with Supabase.
 * Provides common functionality for inserting sensor data.
 * 
 * @param T The sensor data type (must be @Serializable and have uuid and created_at fields that can be set via copy())
 * @param tableName The Supabase table name
 * @param sensorName The sensor name for logging purposes
 */
abstract class BaseSensorService<T : @Serializable Any>(
    protected val tableName: String,
    protected val sensorName: String
) {
    // Create SupabaseHelper internally
    protected val supabaseHelper = SupabaseHelper()
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
     * Insert a single data record to Supabase with concrete type.
     * Uses suspend function for proper coroutine scope management.
     * Each service should implement insert methods that call this with the concrete type.
     */
    protected suspend inline fun <reified TSerializable : @Serializable Any> insertToSupabase(
        data: TSerializable
    ) {
        try {
            supabaseClient.from(tableName).insert(data)
            Log.d(AppConfig.LogTags.PHONE_SUPABASE, "$sensorName sensor data inserted successfully")
        } catch (e: Exception) {
            Log.e(AppConfig.LogTags.PHONE_SUPABASE, "Error inserting $sensorName sensor data: ${e.message}", e)
        }
    }

    /**
     * Insert multiple data records to Supabase with concrete type.
     * Uses suspend function for proper coroutine scope management.
     * Each service should implement insert batch methods that call this with the concrete type.
     */
    protected suspend inline fun <reified TSerializable : @Serializable Any> insertBatchToSupabase(
        dataList: List<TSerializable>
    ) {
        try {
            if (dataList.isEmpty()) {
                Log.w(AppConfig.LogTags.PHONE_SUPABASE, "Empty $sensorName data list, skipping insert")
                return
            }

            supabaseClient.from(tableName).insert(dataList)
            Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Inserted ${dataList.size} $sensorName sensor data entries")
        } catch (e: Exception) {
            Log.e(AppConfig.LogTags.PHONE_SUPABASE, "Error inserting $sensorName sensor data batch: ${e.message}", e)
        }
    }
}

