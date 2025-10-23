package kaist.iclab.loggerstructure.summary

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SkinTemperatureSummary(
    @SerializedName("bucket_start")
    val bucketStart: Long,

    @SerializedName("count")
    val count: Int,

    @SerializedName("avg_object_temp")
    val avgObjectTemp: Double,

    @SerializedName("var_object_temp")
    val varObjectTemp: Double,

    @SerializedName("avg_ambient_temp")
    val avgAmbientTemp: Double,

    @SerializedName("bad_status_count")
    val badStatusCount: Int
) : Serializable