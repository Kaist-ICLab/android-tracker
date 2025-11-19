package kaist.iclab.mobiletracker.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility object for formatting timestamps
 */
object DateTimeFormatter {
    /**
     * Format Unix timestamp (milliseconds) to "YYYY-MM-DD HH:mm:ss" format
     * 
     * @param timestampMillis Unix timestamp in milliseconds
     * @return Formatted timestamp string in "YYYY-MM-DD HH:mm:ss" format (UTC timezone)
     */
    fun formatTimestamp(timestampMillis: Long): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        dateFormat.timeZone = TimeZone.getTimeZone("UTC")
        return dateFormat.format(Date(timestampMillis))
    }
}

