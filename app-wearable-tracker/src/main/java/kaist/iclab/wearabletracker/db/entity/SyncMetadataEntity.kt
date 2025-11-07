package kaist.iclab.wearabletracker.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to store sync metadata.
 * This is a single-row table (always uses id = 1) to store the last successful sync timestamp.
 */
@Entity
data class SyncMetadataEntity(
    @PrimaryKey
    val id: Int = 1, // Always 1 for single-row table
    val lastSyncTimestamp: Long // Unix timestamp in milliseconds
)
