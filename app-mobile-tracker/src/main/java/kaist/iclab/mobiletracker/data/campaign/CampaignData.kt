package kaist.iclab.mobiletracker.data.campaign

import kotlinx.serialization.Serializable

/**
 * Data class representing a campaign from Supabase
 * Note: id is stored as Int in database, but converted to String for use in the app
 */
@Serializable
data class CampaignData(
    val id: Int,
    val name: String,
    val created_at: String? = null
) {
    /**
     * Get id as String for compatibility with UI components
     */
    val idString: String get() = id.toString()
}

