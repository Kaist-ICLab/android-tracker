package kaist.iclab.mobiletracker.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.tracker.auth.Authentication
import kaist.iclab.tracker.auth.User
import kaist.iclab.tracker.auth.UserState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Supabase-based authentication implementation using Google Sign-In.
 * Uses CredentialManager to get Google ID token and authenticates with Supabase Auth.
 */
class SupabaseAuth(
    private val context: Context,
    private val clientId: String,
    private val supabaseHelper: SupabaseHelper
) : Authentication {

    companion object {
        private const val TAG = "SupabaseAuth"
    }

    private val supabaseClient = supabaseHelper.supabaseClient
    private val credentialManager = CredentialManager.create(context)

    private val _userStateFlow = MutableStateFlow<UserState>(
        UserState(isLoggedIn = false, user = null, token = null)
    )
    override val userStateFlow: StateFlow<UserState> = _userStateFlow.asStateFlow()

    init {
        checkCurrentSession()
    }

    /**
     * Check if there's an existing Supabase session
     */
    private fun checkCurrentSession() {
        try {
            supabaseClient.auth.currentSessionOrNull()?.let { session ->
                _userStateFlow.value = createUserStateFromSession(session)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error checking current session: ${e.message}", e)
        }
    }

    override suspend fun getToken() {
        try {
            supabaseClient.auth.currentSessionOrNull()?.let { session ->
                val token = session.getPropertyValue("accessToken") as? String
                _userStateFlow.value = _userStateFlow.value.copy(token = token)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting token: ${e.message}", e)
        }
    }

    override suspend fun login(activity: Activity) {
        val request = buildGoogleIdTokenCredentialRequest()
        try {
            val result = credentialManager.getCredential(
                request = request,
                context = activity
            )
            handleCredential(result.credential)
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Authentication failed: ${e.message}", e)
            _userStateFlow.value = createErrorState("Authentication failed: ${e.message}")
        }
    }

    override suspend fun logout() {
        try {
            // Sign out from Supabase
            supabaseClient.auth.signOut()

            // Clear credential state from all credential providers
            credentialManager.clearCredentialState(
                androidx.credentials.ClearCredentialStateRequest()
            )

            _userStateFlow.value = UserState(
                isLoggedIn = false,
                user = null,
                token = null
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error during logout: ${e.message}", e)
        }
    }

    /**
     * Build the Google ID token credential request
     */
    private fun buildGoogleIdTokenCredentialRequest(): GetCredentialRequest {
        val googleIdOption = GetSignInWithGoogleOption.Builder(clientId).build()
        return GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()
    }

    /**
     * Helper function to safely get a property value using reflection.
     * Tries getter method first (e.g., getEmail), then direct property access (e.g., email).
     */
    private fun Any.getPropertyValue(propertyName: String): Any? {
        return try {
            val capitalized = propertyName.replaceFirstChar { it.uppercaseChar() }
            javaClass.getMethod("get$capitalized").invoke(this)
        } catch (e: NoSuchMethodException) {
            try {
                javaClass.getMethod(propertyName).invoke(this)
            } catch (e2: Exception) {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Extract user name from Supabase user metadata or email
     * Uses reflection to avoid explicit type dependency
     */
    private fun extractUserName(user: Any): String {
        val email = user.getPropertyValue("email") as? String
        val metadata = user.getPropertyValue("userMetadata") as? Map<*, *>?

        return metadata?.get("full_name") as? String
            ?: metadata?.get("name") as? String
            ?: email?.substringBefore("@")
            ?: "No name"
    }

    /**
     * Create UserState from Supabase session
     */
    private fun createUserStateFromSession(session: Any): UserState {
        val supabaseUser = session.getPropertyValue("user")
            ?: return createErrorState("Session has no user")
        
        val accessToken = session.getPropertyValue("accessToken") as? String
        val email = supabaseUser.getPropertyValue("email") as? String

        return UserState(
            isLoggedIn = true,
            user = User(
                email = email ?: "No email",
                name = extractUserName(supabaseUser)
            ),
            token = accessToken
        )
    }

    /**
     * Create error UserState
     */
    private fun createErrorState(message: String): UserState {
        return UserState(
            isLoggedIn = false,
            user = null,
            token = null,
            message = message
        )
    }

    /**
     * Handle the credential from CredentialManager and authenticate with Supabase
     */
    private suspend fun handleCredential(credential: Credential) {
        if (credential !is CustomCredential ||
            credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            Log.e(TAG, "Unexpected credential type: ${credential::class.simpleName}")
            _userStateFlow.value = createErrorState("Unexpected credential type")
            return
        }

        try {
            val googleCredential = GoogleIdTokenCredential.createFrom(credential.data)
            authenticateWithSupabase(googleCredential.idToken)
        } catch (e: Exception) {
            Log.e(TAG, "Error processing Google credential: ${e.message}", e)
            _userStateFlow.value = createErrorState("Failed to process Google credential: ${e.message}")
        }
    }

    /**
     * Authenticate with Supabase using Google ID token
     */
    private suspend fun authenticateWithSupabase(googleIdToken: String) {
        try {
            supabaseClient.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
            }

            val session = supabaseClient.auth.currentSessionOrNull()
                ?: throw Exception("Session not available after sign-in")
            
            _userStateFlow.value = createUserStateFromSession(session)
            Log.d(TAG, "Successfully signed in with Supabase")
        } catch (e: Exception) {
            Log.e(TAG, "Error authenticating with Supabase: ${e.message}", e)
            _userStateFlow.value = createErrorState("Supabase authentication failed: ${e.message}")
        }
    }
}

