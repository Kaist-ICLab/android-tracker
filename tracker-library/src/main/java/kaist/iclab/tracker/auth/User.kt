package kaist.iclab.tracker.auth

data class User(
    val login : Boolean,
    val emailId: String?,
    val token: String?
)
