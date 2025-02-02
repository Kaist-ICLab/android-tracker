package kaist.iclab.tracker.auth

data class UserState(
    val flag: FLAG,
    val user: User? = null,
) {
    enum class FLAG {
        LOGGEDIN,
        LOGGEDOUT
    }
}
