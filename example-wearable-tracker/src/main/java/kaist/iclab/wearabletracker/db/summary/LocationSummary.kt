package kaist.iclab.wearabletracker.db.summary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LocationSummary(
    @SerializedName("bucket_start")
    val bucketStart: Long,

    @SerializedName("count")
    val count: Int,

    // TODO
) : Serializable