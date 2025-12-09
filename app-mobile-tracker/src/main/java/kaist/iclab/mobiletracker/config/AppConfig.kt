package kaist.iclab.mobiletracker.config

/**
 * TODO: Change approach using environment variables
 * Configuration file for test-sync app.
 * Contains all implemented configuration variables.
 * 
 * ⚠️ IMPORTANT: Replace placeholder values with your actual configuration!
 */
object AppConfig {
    /**
     * Supabase project URL
     * Get this from your Supabase project dashboard > Settings > API
     * Example: https://your-project-id.supabase.co
     */
    const val SUPABASE_URL = "http://143.248.53.125:8000/"
    
    /**
     * Supabase anonymous/public key
     * Get this from your Supabase project settings > API > Project API keys
     * This is safe to use in client applications
     */
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYW5vbiIsImlzcyI6InN1cGFiYXNlIiwiaWF0IjoxNzYyODczMjAwLCJleHAiOjE5MjA2Mzk2MDB9.D-Hqc9yhQJo6NHkBSllMzu-P435ay6-L_JYhEfO58TQ"

    /**
     * UUID for sensor data entries
     * TODO: Replace with dynamic UUID generation (UUID.randomUUID().toString()) for production
     * Currently using a hardcoded UUID for testing purposes
     */
    const val SENSOR_DATA_UUID = "c2115b3b-4499-497f-ad65-5832290e7c30"

    /**
     * Supabase table names for sensor data
     * Centralized location for all Supabase table names used in the application
     */
    object SupabaseTables {
        // Watch sensor table names
        const val LOCATION_SENSOR = "location_sensor"
        const val ACCELEROMETER_SENSOR = "accelerometer_sensor"
        const val EDA_SENSOR = "eda_sensor"
        const val HEART_RATE_SENSOR = "heart_rate_sensor"
        const val PPG_SENSOR = "ppg_sensor"
        const val SKIN_TEMPERATURE_SENSOR = "skin_temperature_sensor"

        // Phone sensor table names
        const val AMBIENT_LIGHT_SENSOR = "ambient_light_sensor"
        const val BATTERY_SENSOR = "battery_sensor"
        const val BLUETOOTH_SCAN_SENSOR = "bluetooth_scan_sensor"
        const val SCREEN_SENSOR = "screen_sensor"
        const val WIFI_SCAN_SENSOR = "wifi_scan_sensor"
    }

    /**
     * BLE message keys for different data types
     * These keys are used to identify different types of messages in BLE communication
     */
    object BLEKeys {
        const val SENSOR_DATA_CSV = "sensor_data_csv"
    }

    /**
     * Log tags for different components
     * Use these tags to filter logs: adb logcat | grep "TAG_NAME"
     */
    object LogTags {
        const val PHONE_BLE = "PHONE_BLE"
        const val PHONE_SUPABASE = "PHONE_SUPABASE"
    }
}
