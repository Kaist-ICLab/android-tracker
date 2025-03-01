package kaist.iclab.tracker.auth

data class UserState(
    val isLoggedIn: Boolean,
    val user: User?,
    val token: String?,
    val message: String? = null
)
