package kaist.iclab.mobiletracker.data.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing campaign table data.
 *
 * @property id Unique identifier for the campaign table entry (integer primary key).
 * @property campaignId Campaign identifier.
 * @property name Name of the campaign table.
 * @property description Description of the campaign table.
 * @property dailyCountMax Maximum daily count allowed for the table.
 */
@Serializable
data class CampaignTableData(
    val id: Int? = null,
    val campaignId: Int,
    val name: String,
    val description: String,
    val dailyCountMax: Int
)

