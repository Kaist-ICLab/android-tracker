package kaist.iclab.tracker

import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.Build
import android.provider.Settings

class TrackerUtil(
    private val context: Context) {
    fun getDeviceModel() = Build.MODEL
//  Device Name (e.g., Galaxy S10) is hard to retrieve
    fun getAppVersion(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName,0)
            return pInfo.versionName
        } catch (e: Exception) {
            "Unknown"
        }
    }
    fun getOSVersion() = Build.VERSION.RELEASE
    fun getDeviceUUID(): String = Settings.Secure.ANDROID_ID
    fun isPreinstalledApp(): String {
        return try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if(pInfo.applicationInfo.flags and (ApplicationInfo.FLAG_SYSTEM or
            ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                return "PREINSTALLED"
            }else{
                return "USER_INSTALLED"
            }
        } catch (e: Exception) {
            return "UNKNOWN"
        }
    }
}