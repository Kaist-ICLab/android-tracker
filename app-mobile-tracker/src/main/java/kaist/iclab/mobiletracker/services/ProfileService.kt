package kaist.iclab.mobiletracker.services

import android.util.Log
import io.github.jan.supabase.postgrest.from
import kaist.iclab.mobiletracker.config.AppConfig
import kaist.iclab.mobiletracker.data.sensors.phone.ProfileData
import kaist.iclab.mobiletracker.helpers.SupabaseHelper
import kaist.iclab.mobiletracker.repository.Result
import kaist.iclab.mobiletracker.repository.runCatchingSuspend

/**
 * Service for handling profile data operations with Supabase
 */
class ProfileService(
    private val supabaseHelper: SupabaseHelper
) {
    private val supabaseClient = supabaseHelper.supabaseClient
    private val tableName = "profiles"
    
    companion object {
        private const val TAG = "ProfileService"
    }
    
    /**
     * Check if a profile exists for the given UUID
     * @param uuid The UUID to check
     * @return Result containing true if exists, false otherwise, or error
     */
    suspend fun profileExists(uuid: String): Result<Boolean> {
        return runCatchingSuspend {
            try {
                val response = supabaseClient.from(tableName)
                    .select {
                        filter {
                            eq("uuid", uuid)
                        }
                    }
                val profiles = response.decodeList<ProfileData>()
                val exists = profiles.isNotEmpty()
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Profile exists check for UUID $uuid: $exists")
                exists
            } catch (e: Exception) {
                Log.e(TAG, "Error checking if profile exists (UUID: $uuid): ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * Create or update a profile in Supabase
     * If profile exists, it will be updated (upsert behavior)
     * @param profile The profile data to save
     * @return Result containing Unit on success or error
     */
    private suspend fun saveProfile(profile: ProfileData): Result<Unit> {
        return runCatchingSuspend {
            try {
                // Use upsert to insert if not exists, update if exists
                supabaseClient.from(tableName).upsert(profile)
                Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Profile saved successfully for UUID: ${profile.uuid}, Email: ${profile.email}")
            } catch (e: Exception) {
                Log.e(TAG, "Error saving profile (UUID: ${profile.uuid}, Email: ${profile.email}): ${e.message}", e)
                throw e
            }
        }
    }
    
    /**
     * Create a profile if it doesn't exist
     * @param uuid The user UUID
     * @param email The user email
     * @param campaignId Optional campaign ID (can be null)
     * @return Result containing Unit on success or error
     */
    suspend fun createProfileIfNotExists(
        uuid: String,
        email: String,
        campaignId: Int? = null
    ): Result<Unit> {
        return runCatchingSuspend {
            try {
                // Check if profile exists
                val existsResult = profileExists(uuid)
                val exists = when (existsResult) {
                    is Result.Success -> existsResult.data
                    is Result.Error -> throw existsResult.exception
                }
                
                if (!exists) {
                    // Create new profile
                    val profile = ProfileData(
                        uuid = uuid,
                        email = email,
                        campaign_id = campaignId
                    )
                    saveProfile(profile)
                    Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Created new profile for UUID: $uuid, Email: $email")
                } else {
                    Log.d(AppConfig.LogTags.PHONE_SUPABASE, "Profile already exists for UUID: $uuid, skipping creation")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error creating profile if not exists (UUID: $uuid, Email: $email): ${e.message}", e)
                throw e
            }
        }
    }
}

