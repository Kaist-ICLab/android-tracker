package kaist.iclab.wearablelogger.data

import android.annotation.SuppressLint
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class WearableData(
    val timestamp: Timestamp,
    val acc: ACC,
    val ppg: PPG,
    val hr: HR,
    val ibi: IBI
) {
    data class ACC(val x: Int, val y: Int, val z: Int) {
        @SuppressLint("DefaultLocale")
        override fun toString(): String {
            val scale = 1 / (16383.75 / 4.0)
            return String.format(
                "ACC = (X = %.1f g, Y = %.2f g, Z = %.2f g)",
                x.toFloat() * scale,
                y.toFloat() * scale,
                z.toFloat() * scale
            )
        }
    }

    data class PPG(val ppg: Int, val status: Int) {
        override fun toString(): String {
            val statusString = when (status) {
                -999 -> "A higher priority sensor is operating. E.g. BIA\n"
                0 -> "Normal value"
                500 -> "STATUS is not supported"
                else -> "UNKNOWN"
            }
            val decimalFormat = DecimalFormat("#,###")
            return "PPG = ${decimalFormat.format(ppg)} (${statusString})"
        }
    }

    data class HR(val hr: Int, val status: Int) {
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
                else -> "UNKNOWN"
            }
            return "HR = $hr BPM ($statusString)"
        }
    }

    data class IBI(val ibi: List<Int>, val status: List<Int>) {
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

    data class Timestamp(val timestamp: Long) {
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