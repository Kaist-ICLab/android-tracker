package dev.iclab.test_auth

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kaist.iclab.tracker.auth.Authentication
import kaist.iclab.tracker.auth.UserState
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class AuthViewModel(
    private val authentication: Authentication) :ViewModel() {
    val userState: StateFlow<UserState> = authentication.userStateFlow

    fun login(activity: Activity) {
        viewModelScope.launch {
            authentication.login(activity)
        }
    }

    fun logout() {
        viewModelScope.launch {
            authentication.logout()
        }
    }

    fun getToken() {
        viewModelScope.launch {
            authentication.getToken()
        }
    }
}
