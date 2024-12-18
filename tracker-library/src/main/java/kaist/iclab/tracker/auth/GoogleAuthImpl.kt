package kaist.iclab.tracker.auth

import android.app.Activity
import android.content.Context
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class GoogleAuthImpl(private val context: Context) : AuthInterface {

    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private val _userFlow = MutableStateFlow<User?>(null)
    override val userFlow: StateFlow<User?> get() = _userFlow

    init {
        firebaseAuth.addAuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            _userFlow.value = firebaseUser?.let { user ->
                User(user.uid, user.email ?: "")
            }
        }
    }

    override fun getUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    override suspend fun login(activity: Activity) {

    }

    override suspend fun getToken(): String? {
        return firebaseAuth.currentUser?.getIdToken(false)?.result?.token
    }

    override suspend fun login(activity: Activity, signInLauncher: ActivityResultLauncher<Intent>) {
        val signInIntent = googleSignInClient.signInIntent
        signInLauncher.launch(signInIntent)
    }

    fun handleSignInResult(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        try {
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { idToken ->
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                firebaseAuth.signInWithCredential(credential).addOnCompleteListener { authTask ->
                    if (!authTask.isSuccessful) {
                        // Handle login failure
                        throw authTask.exception ?: Exception("Login failed")
                    }
                }
            }
        } catch (e: ApiException) {
            // Handle error
            throw e
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
        googleSignInClient.signOut()
        _userFlow.value = null
    }
}
