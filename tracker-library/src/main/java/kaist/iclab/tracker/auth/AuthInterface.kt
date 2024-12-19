package kaist.iclab.tracker.auth

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface AuthInterface {
    val userFlow: StateFlow<User?>

    /*Token used for authentication w/ Server*/
    suspend fun getToken(): String?
    /*Login & Signin*/
    suspend fun login(activity: Activity)
    /*Logout*/
    suspend fun logout()
}