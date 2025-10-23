package kaist.iclab.wearabletracker.db.summary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class AccelerometerSummary(
    @SerializedName("bucket_start")
    val bucketStart: Long,

    @SerializedName("count")
    val count: Int,

    @SerializedName("avg")
    val avg: Double,

    @SerializedName("variance")
    val variance: Double
) : Serializable