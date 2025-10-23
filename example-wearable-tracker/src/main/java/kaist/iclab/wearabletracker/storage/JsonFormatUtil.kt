package kaist.iclab.wearabletracker.storage

import com.google.gson.JsonObject
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.math.sqrt

object JsonFormatUtil {
    private val TIME_PROPERTY = listOf("bucket_start")
    private val VARIANCE_PROPERTY = mapOf(
        "variance" to "std_dev",
        "var_ppg_red" to "std_dev_ppg_red",
        "var_object_temp" to "std_dev_object_temp"
    )

    fun JsonObject.formatForUpload() {
        for(prop in TIME_PROPERTY) {
            if(this.has(prop))
                this.addProperty(prop, toZonedTimestamp(this[prop].asLong))
        }

        VARIANCE_PROPERTY.forEach { (from, newName) ->
            if(this.has(from)) {
                val value = this[from].asDouble
                this.addProperty(newName, sqrt(value))
                this.remove(from)
            }
        }

        this.addProperty("device_id", "dui")
        this.remove("id")
    }

    private fun toZonedTimestamp(timeMillis: Long): String {
        val kstTime = ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(timeMillis),
            ZoneId.of("Asia/Seoul")
        )

        val string = kstTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        return string
    }
}