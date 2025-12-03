package kaist.iclab.mobiletracker.services

import android.util.Log
import io.github.jan.supabase.postgrest.from
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.repository.runCatchingSuspend
import kotlinx.serialization.Serializable

/**
 * Base service class for handling watch sensor data uploads to Supabase.
 * 
 * This class provides common functionality for uploading sensor data from wearable devices
 * to the remote Supabase database. All watch sensor services extend this class.
 * 
 * For local phone sensor data storage, see `PhoneSensorDataService`.
 * 
 * @param T The sensor data type (must be @Serializable and have uuid and created_at fields that can be set via copy())
 * @param supabaseHelper The SupabaseHelper instance (injected via DI)
 * @param tableName The Supabase table name
 * @param sensorName The sensor name for logging purposes
 */
abstract class BaseSensorService<T : @Serializable Any>(
    protected val supabaseHelper: SupabaseHelper,
    protected val tableName: String,
    protected val sensorName: String
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
     * Insert a single data record to Supabase with concrete type.
     * Uses suspend function for proper coroutine scope management.
     * Returns Result type for explicit error handling.
     * Each service should implement insert methods that call this with the concrete type.
     */
    protected suspend inline fun <reified TSerializable : @Serializable Any> insertToSupabase(
        data: TSerializable
    ): Result<Unit> {
        return runCatchingSuspend {
            supabaseClient.from(tableName).insert(data)
            Log.d(AppConfig.LogTags.PHONE_SUPABASE, "$sensorName sensor data inserted successfully")
        }
    }

    /**
     * Insert multiple data records to Supabase with concrete type.
     * Uses suspend function for proper coroutine scope management.
     * Returns Result type for explicit error handling.
     * Each service should implement insert batch methods that call this with the concrete type.
     */
    protected suspend inline fun <reified TSerializable : @Serializable Any> insertBatchToSupabase(
        dataList: List<TSerializable>
    ): Result<Unit> {
        return runCatchingSuspend {
            if (dataList.isEmpty()) {
                Log.w(AppConfig.LogTags.PHONE_SUPABASE, "Empty $sensorName data list, skipping insert")
                return@runCatchingSuspend
            }

            supabaseClient.from(tableName).insert(dataList)
            Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Inserted ${dataList.size} $sensorName sensor data entries")
        }
    }
}

