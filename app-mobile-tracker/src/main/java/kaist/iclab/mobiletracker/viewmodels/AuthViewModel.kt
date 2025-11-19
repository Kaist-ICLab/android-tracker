package kaist.iclab.mobiletracker.viewmodels

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.tracker.auth.Authentication
import kaist.iclab.tracker.auth.UserState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authentication: Authentication,
    private val context: Context
) : ViewModel() {
    private val TAG = "AuthViewModel"
    
    companion object {
        private const val PREFS_NAME = "auth_preferences"
        private const val KEY_AUTH_TOKEN = "auth_token"
    }
    
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        PREFS_NAME,
        Context.MODE_PRIVATE
    )

    val userState: StateFlow<UserState> = authentication.userStateFlow
    
    private var previousLoginState = false
    private var lastSavedToken: String? = null

    init {
        // Observe userState changes to get token after login and save it automatically
        viewModelScope.launch {
            userState.collect { state ->
                val currentToken = state.token
                
                // When user successfully logs in, automatically get token
                if (state.isLoggedIn && !previousLoginState && currentToken == null) {
                    previousLoginState = true
                    authentication.getToken()
                }
                
                // When token becomes available, save it to SharedPreferences and log it (only once per token)
                if (state.isLoggedIn && currentToken != null && currentToken != lastSavedToken) {
                    saveTokenToPreferences(currentToken)
                    lastSavedToken = currentToken
                }
                
                // Reset flags when user logs out
                if (!state.isLoggedIn) {
                    previousLoginState = false
                    lastSavedToken = null
                }
            }
        }
    }

    fun login(activity: Activity) {
        viewModelScope.launch {
            try {
                authentication.login(activity)
            } catch (e: Exception) {
                Log.e(TAG, "Login error: ${e.message}", e)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authentication.logout()
            clearTokenFromPreferences()
        }
    }

    fun getToken() {
        viewModelScope.launch {
            authentication.getToken()
        }
    }
    
    private fun saveTokenToPreferences(token: String) {
        sharedPreferences.edit()
            .putString(KEY_AUTH_TOKEN, token)
            .apply()
    }
    
    private fun clearTokenFromPreferences() {
        sharedPreferences.edit()
            .remove(KEY_AUTH_TOKEN)
            .apply()
    }
}