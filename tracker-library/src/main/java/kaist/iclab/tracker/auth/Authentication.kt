package kaist.iclab.tracker.auth

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface Authentication {
    val userStateFlow: StateFlow<UserState>

    suspend fun getToken()
    suspend fun login(activity: Activity)
    suspend fun logout()
}