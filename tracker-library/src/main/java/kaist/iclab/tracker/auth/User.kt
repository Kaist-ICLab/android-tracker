package kaist.iclab.tracker.auth

//data class User(
////    val login : Boolean,
//    val email: String?,
//    val name: String?,
////    val token: String?
//)

data class User(
    val email: String,
    val name: String,
    val gender: String,
    val birthDate: String,
    val age: Int,
    val experimentGroup: String? = null
)
