package kaist.iclab.wearabletracker.db.summary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class EDASummary(
    @SerializedName("bucket_start")
    val bucketStart: Long,

    @SerializedName("count")
    val count: Int,

    @SerializedName("avg")
    val avg: Double,

    @SerializedName("variance")
    val variance: Double,

    @SerializedName("bad_status_count")
    val badStatusCount: Int
) : Serializable