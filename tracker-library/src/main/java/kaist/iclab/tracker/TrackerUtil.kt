package kaist.iclab.tracker

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TrackerUtil {
    @SuppressLint("DefaultLocale")
    fun Long.formatLapsedTime(): String {
        val seconds = this / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d.%03d", hours, minutes % 60, seconds % 60, this % 1000)
    }

    fun Long.formatLocalDateTime(): String {
        val date = Date(this)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREAN)
        return format.format(date)
    }
}