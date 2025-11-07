package kaist.iclab.mobiletracker.data.phone

import kotlinx.serialization.Serializable

/**
 * Supabase data class representing campaign table user daily summary data.
 *
 * @property id Unique identifier for the daily summary entry (integer primary key).
 * @property uuid Unique identifier for the user (UUID format).
 * @property hourlyCount0 Hourly count for hour 0.
 * @property hourlyCount1 Hourly count for hour 1.
 * @property hourlyCount2 Hourly count for hour 2.
 * @property hourlyCount3 Hourly count for hour 3.
 * @property hourlyCount4 Hourly count for hour 4.
 * @property hourlyCount5 Hourly count for hour 5.
 * @property hourlyCount6 Hourly count for hour 6.
 * @property hourlyCount7 Hourly count for hour 7.
 * @property campaignTableId Campaign table identifier.
 * @property day Date of the summary (format: YYYY-MM-DD).
 */
@Serializable
data class CampaignTableUserDailySummaryData(
    val id: Int? = null,
    val uuid: String? = null,
    val hourlyCount0: Int,
    val hourlyCount1: Int,
    val hourlyCount2: Int,
    val hourlyCount3: Int,
    val hourlyCount4: Int,
    val hourlyCount5: Int,
    val hourlyCount6: Int,
    val hourlyCount7: Int,
    val campaignTableId: Int,
    val day: String
)

