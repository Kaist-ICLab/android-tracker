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
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJyb2xlIjoiYW5vbiIsImlzcyI6InN1cGFiYXNlIiwiaWF0IjoxNzYyODczMjAwLCJleHAiOjE5MjA2Mzk2MDB9.HeSRX34m7EvdeTBJR7ODmv0SWHs6vVBj1MgQsS8fogQ"

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
