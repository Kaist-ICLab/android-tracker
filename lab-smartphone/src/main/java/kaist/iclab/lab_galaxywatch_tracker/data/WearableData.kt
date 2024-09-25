package kaist.iclab.lab_galaxywatch_tracker.data

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WearableData(
    val timestamp: Timestamp = Timestamp(),
    val acc: ACC = ACC(),
    val ppg: PPG = PPG(),
    val hr: HR = HR(),
    val ibi: IBI = IBI()
) {
    data class ACC(val x: Int= 0, val y: Int = 0, val z: Int=0) {
        @SuppressLint("DefaultLocale")
        override fun toString(): String {
            val scale = 1 / (16383.75 / 4.0)
            return String.format(
                "ACC = (X = %.2f g, Y = %.2f g, Z = %.2f g)",
                x.toFloat() * scale,
                y.toFloat() * scale,
                z.toFloat() * scale
            )
        }
    }

    data class PPG(val ppg: Int = 0, val status: Int = -1) {
        override fun toString(): String {
            val statusString = when (status) {
                -999 -> "A higher priority sensor is operating. E.g. BIA\n"
                0 -> "Normal value"
                500 -> "STATUS is not supported"
                -1 -> "READY"
                else -> "UNKNOWN"
            }
            val decimalFormat = DecimalFormat("#,###")
            return "PPG = ${decimalFormat.format(ppg)} (${statusString})"
        }
    }

    data class HR(val hr: Int = 0, val status: Int = 2) {
        override fun toString(): String {
            val statusString = when (status) {
                -999 -> "A higher priority sensor is operating. E.g. BIA\n"
                -99 -> "flush() is called but no data"
                -10 -> "PPG signal is too week"
                -8 -> "PPG signal is weak"
                -3 -> "Wearable is detached"
                -2 -> "Wearabled movement is detected"
                0 -> "Initial heart reate measuring state or a higher priority sensor is operating. E.g. BIA"
                1 -> "Heart rate is being measured"
                2 -> "READY"
                else -> "UNKNOWN"
            }
            return "HR = $hr BPM ($statusString)"
        }
    }

    data class IBI(val ibi: List<Int> = listOf(), val status: List<Int> = listOf()) {
        override fun toString(): String {
            if(ibi.size != status.size) return "IBI = UNKNOWN"
            if(ibi.size == 0) return "IBI = -"
            val statusString = when (status.last()) {
                -1 -> "Error"
                0 -> "Normal"
                else -> "UNKNOWN"
            }
            return "IBI = ${ibi.last()} ms ($statusString)"
        }
    }

    data class Timestamp(val timestamp: Long = System.currentTimeMillis()) {
        override fun toString(): String {
            val date = Date(timestamp)
            val calendar = Calendar.getInstance()
            calendar.setTime(date)

            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            dateFormat.setTimeZone(TimeZone.getDefault())

            return dateFormat.format(calendar.getTime())
        }
    }
}