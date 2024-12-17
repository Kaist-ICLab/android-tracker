package kaist.iclab.tracker.auth

import kotlinx.coroutines.flow.Flow

class GoogleAuthImpl: AuthInterface {
    override fun userFlow(): Flow<User> {
        TODO("Not yet implemented")
    }

    override fun getUserEmail(): String? {
        TODO("Not yet implemented")
    }

    override fun getToken(): String? {
        TODO("Not yet implemented")
    }

    override suspend fun login() {
        TODO("Not yet implemented")
    }

    override suspend fun logout() {
        TODO("Not yet implemented")
    }
}