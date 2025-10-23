package kaist.iclab.wearabletracker.db.summary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PPGSummary(
    @SerializedName("bucket_start")
    val bucketStart: Long,

    @SerializedName("count")
    val count: Int,

    @SerializedName("avg_ppg_green")
    val avgPpgGreen: Double,

    @SerializedName("avg_ppg_ir")
    val avgPpgIR: Double,

    @SerializedName("avg_ppg_red")
    val avgPpgRed: Double,

    @SerializedName("var_ppg_red")
    val varPpgRed: Double,

    @SerializedName("bad_status_count")
    val badStatusCount: Int
) : Serializable