package kaist.iclab.tracker.controller

open class DataEntity(
    open val received: Long,
    open val deviceId: String = Util.getDeviceId(),
    open val app: String = Util.getApp() + ":" + Util.getAppVersion(),
    open val androidVersion: String = Util.getOSVersion()
)

object Util {
    fun getDeviceModel() = android.os.Build.MODEL
    fun getApp() = "kaist.iclab.tracker"
    fun getAppVersion() = "1.0.0"
    fun getOSVersion() = android.os.Build.VERSION.RELEASE
    fun getDeviceId() = android.provider.Settings.Secure.ANDROID_ID
}