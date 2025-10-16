package com.example.test_sync.config

/**
 * Configuration file for test-sync app.
 * Contains all important keys, URLs, and configuration variables.
 * 
 * ⚠️ IMPORTANT: Replace placeholder values with your actual configuration!
 */
object AppConfig {
    
    // ==================== SUPABASE CONFIGURATION ====================
    
    /**
     * Supabase project URL
     * Get this from your Supabase project dashboard
     */
    const val SUPABASE_URL = "https://urtvamywsnujfmcdnnxb.supabase.co"
    
    /**
     * Supabase anonymous/public key
     * Get this from your Supabase project settings > API
     */
    const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InVydHZhbXl3c251amZtY2RubnhiIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTA5MTExMDYsImV4cCI6MjA2NjQ4NzEwNn0.VIGwIg_IRqJuw2Vqqk8SM8raWC2AjryMSZSwnTrmBaI"
    
    /**
     * Supabase table name for test data
     */
    const val SUPABASE_TABLE_NAME = "test_data"
    
    // ==================== INTERNET/HTTP CONFIGURATION ====================
    
    /**
     * HTTPBin URL for testing HTTP requests
     * HTTPBin is a simple HTTP request & response service
     */
    const val HTTPBIN_URL = "https://httpbin.org"
    
    /**
     * Custom test server URL (if you have one)
     */
    const val CUSTOM_SERVER_URL = "https://your-custom-server.com"
    
    /**
     * Default timeout for HTTP requests (in seconds)
     */
    const val HTTP_TIMEOUT_SECONDS = 30L
    
    // ==================== BLE CONFIGURATION ====================
    
    /**
     * BLE message keys for different data types
     */
    object BLEKeys {
        const val MESSAGE = "message"
        const val STRUCTURED_DATA = "structured_data"
        const val URGENT_MESSAGE = "urgent_message"
    }
    
    /**
     * BLE test messages
     */
    object BLETestMessages {
        const val HELLO_STRING = "HELLO STRING FROM PHONE"
        const val HELLO_WATCH = "HELLO STRING FROM WATCH"
        const val URGENT_ALERT = "URGENT ALERT FROM PHONE"
    }
    
    // ==================== LOGGING CONFIGURATION ====================
    
    /**
     * Log tags for different components
     */
    object LogTags {
        const val PHONE_BLE = "PHONE_BLE"
        const val PHONE_INTERNET = "PHONE_INTERNET"
        const val PHONE_SUPABASE = "PHONE_SUPABASE"
        const val WATCH_BLE = "WATCH_BLE"
    }
    
    // ==================== TEST DATA CONFIGURATION ====================
    
    /**
     * Default test data values
     */
    object TestData {
        const val DEFAULT_MESSAGE = "Test Message"
        const val DEFAULT_VALUE = 123
        const val DEFAULT_URL = "https://httpbin.org/post"
    }
    
    // ==================== DEVELOPMENT CONFIGURATION ====================
    
    /**
     * Enable/disable debug logging
     */
    const val DEBUG_LOGGING_ENABLED = true
    
    /**
     * Enable/disable BLE debugging
     */
    const val BLE_DEBUG_ENABLED = true
    
    /**
     * Enable/disable Supabase debugging
     */
    const val SUPABASE_DEBUG_ENABLED = true
    
    /**
     * Enable/disable Internet debugging
     */
    const val INTERNET_DEBUG_ENABLED = true
}
