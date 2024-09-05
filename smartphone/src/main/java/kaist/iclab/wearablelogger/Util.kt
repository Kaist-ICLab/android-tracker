import android.annotation.SuppressLint


@SuppressLint("DefaultLocale")
fun Long.formatLapsedTime(): String {
    val seconds = this / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    return String.format("%02d:%02d:%02d.%03d", hours, minutes % 60, seconds % 60, this % 1000)
}
