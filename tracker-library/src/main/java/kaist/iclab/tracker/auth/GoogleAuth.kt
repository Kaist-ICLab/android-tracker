package kaist.iclab.tracker.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class GoogleAuth(
    private val context: Context,
    private val clientId: String,
) : Authentication {
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
        val token =  auth.currentUser?.getIdToken(false)?.await()?.token
        if(token != null){
            _userStateFlow.value = UserState(isLoggedIn = true, user = auth.currentUser.toUser(), token = token)
        }
    }

    override suspend fun login(activity: Activity) {
        val request = buildGoogleIdTokenCredentialRequest()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val credential = CredentialManager.create(context)
                    .getCredential(activity, request).credential
                handleCredential(credential)
            } catch (e: GetCredentialException) {
                Log.e("GoogleAuth", "FAILURE $e")
//                Toast.makeText(activity, "FAILURE $e", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override suspend fun logout() {
        auth.signOut()
    }


    private fun buildGoogleIdTokenCredentialRequest(): GetCredentialRequest {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(clientId)
            .setAutoSelectEnabled(true)
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
                Log.d("Google Auth", "signInWithCredential:success")
            } else {
                Log.w("Google Auth", "signInWithCredential:failure", task.exception)
            }
        }
    }

    private fun handleCredential(credential: Credential) {
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            firebaseAuthWithGoogle(googleCredential.idToken)
        } else {
            Log.w("Google Auth", "Credential is not of type Google ID!")
            throw IllegalArgumentException("Invalid Credential")
        }
    }

    private fun FirebaseUser?.toUser(): User? {
        return this?.let {
            User(
                email = it.email!!,
                name = it.displayName!!
            )
        }
    }
}