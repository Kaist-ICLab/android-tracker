package kaist.iclab.mobiletracker.viewmodels.auth

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.mobiletracker.repository.AuthRepository
import kaist.iclab.tracker.auth.Authentication
import kaist.iclab.tracker.auth.UserState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authentication: Authentication,
    private val authRepository: AuthRepository
) : ViewModel() {
    private val TAG = "AuthViewModel"

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
                
                // When token becomes available, save it to repository and log it (only once per token)
                if (state.isLoggedIn && currentToken != null && currentToken != lastSavedToken) {
                    authRepository.saveToken(currentToken)
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
            authRepository.clearToken()
        }
    }

    fun getToken() {
        viewModelScope.launch {
            authentication.getToken()
        }
    }
}

