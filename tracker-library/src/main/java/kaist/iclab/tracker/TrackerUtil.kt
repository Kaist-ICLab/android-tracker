package kaist.iclab.tracker

import android.annotation.SuppressLint

object TrackerUtil {
    fun getDeviceModel() = android.os.Build.MODEL
    fun getApp() = "kaist.iclab.tracker"
    fun getAppVersion() = "1.0.0"
    fun getOSVersion() = android.os.Build.VERSION.RELEASE
    fun getDeviceId() = android.provider.Settings.Secure.ANDROID_ID


    @SuppressLint("DefaultLocale")
    fun Long.formatLapsedTime(): String {
        val seconds = this / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        return String.format("%02d:%02d:%02d.%03d", hours, minutes % 60, seconds % 60, this % 1000)
    }
}