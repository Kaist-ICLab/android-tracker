package kaist.iclab.field_tracker

interface Util {
    fun getDeviceUUID(): String
    fun getOSVersion(): String
    fun getDeviceModel(): String
    fun getDeviceName(): String
    fun getAppVersion(): String
}