package kaist.iclab.field_tracker.auth

import kotlinx.coroutines.flow.Flow

interface AuthInterface {
    fun userFlow(): Flow<User>
    fun getUserEmail(): String?
    /*Token used for authentication w/ Server*/
    fun getToken(): String?
    /*Login & Signin*/
    suspend fun login()
    /*Logout*/
    suspend fun logout()

    data class User(
        val login : Boolean,
        val emailId: String?,
        val token: String?
    )
}