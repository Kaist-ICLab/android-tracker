//package kaist.iclab.tracker.auth
//
//import android.app.Activity
//import android.content.Context
//import android.util.Log
//import androidx.credentials.Credential
//import androidx.credentials.CredentialManager
//import androidx.credentials.CustomCredential
//import androidx.credentials.GetCredentialRequest
//import androidx.credentials.exceptions.GetCredentialException
//import com.google.android.libraries.identity.googleid.GetGoogleIdOption
//import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
//import com.google.firebase.Firebase
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.FirebaseUser
//import com.google.firebase.auth.GoogleAuthProvider
//import com.google.firebase.auth.auth
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//
//
//class GoogleAuthImpl(private val context: Context) : AuthInterface {
//
//    private val TAG = javaClass.simpleName
//
//    private val auth: FirebaseAuth = Firebase.auth
//    private var authStateListener: FirebaseAuth.AuthStateListener? = null
//
//    private val _userFlow = MutableStateFlow<User?>(null)
//    override val userFlow: StateFlow<User?> get() = _userFlow
//
//    init {
//        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
//            val user = firebaseAuth.currentUser.toUser()
//            _userFlow.value = user
//        }
//        auth.addAuthStateListener(authStateListener!!)
//    }
//
//    override suspend fun login(activity: Activity){
//        val request = buildGoogleIdTokenCredentialRequest()
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                val credential  = CredentialManager.create(context)
//                    .getCredential(activity, request).credential
//                handleCredential(credential)
//            } catch (e: GetCredentialException) {
//                Log.e(TAG, "FAILURE $e")
////                TODO: UI-level로의 Notify?
//            }
//        }
//    }
//
//    override suspend fun logout() {
//        auth.signOut()
//    }
//
//
//
//    override suspend fun getToken(): String? {
//        val token = auth.getAccessToken(true)
//        val result = token.await()
//        return result.token
//    }
//
//
//    private fun buildGoogleIdTokenCredentialRequest(): GetCredentialRequest {
//        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
//            .setFilterByAuthorizedAccounts(false)
////            .setServerClientId(BuildConfig.GOOGLE_CREDENTIAL_ID)
//            .setAutoSelectEnabled(true)
//            .build()
////        OAuth2.0 Client ID for Web Application
//        val request = GetCredentialRequest.Builder()
//            .addCredentialOption(googleIdOption)
//            .build()
//        return request
//    }
//    private fun handleCredential(credential: Credential){
//        if(credential is CustomCredential &&
//            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
//            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
//            val firebaseCredential =
//                GoogleAuthProvider.getCredential(googleCredential.idToken, null)
//            auth.signInWithCredential(firebaseCredential)
//        }else{
//            throw IllegalArgumentException("Invalid Credential")
//        }
//    }
//
//    private fun FirebaseUser?.toUser(): User? {
//        return this?.let {
//            User(
//                email = it.email,
//                name = it.displayName
//            )
//        }
//    }
//}
