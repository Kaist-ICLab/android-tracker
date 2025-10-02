package kaist.iclab.tracker.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class GoogleAuth(
    private val context: Context,
    private val clientId: String,
) : Authentication {

    companion object {
        private const val TAG = "GoogleAuth"
    }

    private val auth: FirebaseAuth = Firebase.auth
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    private val _userStateFlow =
        MutableStateFlow(UserState(isLoggedIn = false, user = null, token = null))
    override val userStateFlow: StateFlow<UserState> = _userStateFlow

    init {
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser.toUser()
            _userStateFlow.value = UserState(isLoggedIn = user != null, user = user, token = null)
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    override suspend fun getToken() {
        val token = auth.currentUser?.getIdToken(false)?.await()?.token
        if (token != null) {
            _userStateFlow.value =
                UserState(isLoggedIn = true, user = auth.currentUser.toUser(), token = token)
        }
    }

    override suspend fun login(activity: Activity) {
        val request = buildGoogleIdTokenCredentialRequest()
        try {
            val result = CredentialManager.create(context)
                .getCredential(activity, request)
            handleCredential(result.credential)
        } catch (e: GetCredentialException) {
            _userStateFlow.value = UserState(
                isLoggedIn = false,
                user = null,
                token = null,
                message = "Authentication failed: ${e.message}"
            )
        }
    }

    override suspend fun logout() {
        try {
            // Clear Firebase auth state
            auth.signOut()

            // Clear credential state from all credential providers
            CredentialManager.create(context).clearCredentialState(
                androidx.credentials.ClearCredentialStateRequest()
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout: $e")
        }
    }


    private fun buildGoogleIdTokenCredentialRequest(): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // Allow new users to sign up
            .setServerClientId(clientId)
            .setAutoSelectEnabled(false) // Let user choose account
            .build()
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
        return request
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                _userStateFlow.value = UserState(
                    isLoggedIn = true,
                    user = user.toUser(),
                    token = null
                )
            } else {
                _userStateFlow.value = UserState(
                    isLoggedIn = false,
                    user = null,
                    token = null,
                    message = "Firebase authentication failed: ${task.exception?.message}"
                )
            }
        }
    }

    private fun handleCredential(credential: Credential) {
        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
                        firebaseAuthWithGoogle(googleCredential.idToken)
                    } catch (e: Exception) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                        _userStateFlow.value = UserState(
                            isLoggedIn = false,
                            user = null,
                            token = null,
                            message = "Invalid Google ID token: ${e.message}"
                        )
                    }
                } else {
                    Log.e(TAG, "Unexpected type of credential")
                    _userStateFlow.value = UserState(
                        isLoggedIn = false,
                        user = null,
                        token = null,
                        message = "Unexpected credential type"
                    )
                }
            }

            else -> {
                Log.e(TAG, "Unexpected type of credential")
                _userStateFlow.value = UserState(
                    isLoggedIn = false,
                    user = null,
                    token = null,
                    message = "Unexpected credential type"
                )
            }
        }
    }

    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            User(
                email = it.email ?: "No email",
                name = it.displayName ?: "No name"
            )
        }
    }
}