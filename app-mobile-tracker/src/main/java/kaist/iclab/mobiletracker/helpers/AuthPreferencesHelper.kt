package kaist.iclab.mobiletracker.helpers

import android.content.Context
import android.content.SharedPreferences

/**
 * Helper class for managing authentication-related SharedPreferences.
 * Handles storing and retrieving authentication tokens.
 */
class AuthPreferencesHelper(private val context: Context) {
    
    companion object {
        private const val PREFS_NAME = "auth_preferences"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )
    
    /**
     * Save authentication token to SharedPreferences
     */
    fun saveToken(token: String) {
        sharedPreferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }
    
    /**
     * Get authentication token from SharedPreferences
     * @return The saved token, or null if not found
     */
    fun getToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }
    
    /**
     * Clear authentication token from SharedPreferences
     */
    fun clearToken() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .apply()
    }
    
    /**
     * Check if a token exists in SharedPreferences
     */
    fun hasToken(): Boolean {
        return getToken() != null
    }
}

