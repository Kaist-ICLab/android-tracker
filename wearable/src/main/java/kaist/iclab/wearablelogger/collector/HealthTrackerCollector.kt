package kaist.iclab.wearablelogger.collector

import android.Manifest
import android.content.Context
import android.os.Build
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import kaist.iclab.wearablelogger.config.Util

abstract class HealthTrackerCollector(
    private val context: Context
): CollectorInterface {
    override val TAG = javaClass.simpleName
    var tracker: HealthTracker? = null
    abstract val trackerEventListener: HealthTracker.TrackerEventListener
    abstract fun initHealthTracker()

    override fun setup() {}
    override fun isAvailable(): Boolean {
            return Util.isPermissionAllowed(
                context, listOfNotNull(
                    Manifest.permission.BODY_SENSORS,
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
                    Manifest.permission.ACTIVITY_RECOGNITION
                )
            )
    }
    override fun startLogging() {
        try{
            initHealthTracker()
            tracker?.setEventListener(trackerEventListener)
        }catch(e: Exception){
            Log.e(TAG, "SkinTempCollector startLogging: $e")
        }
    }
    override fun stopLogging() {
        Log.d(TAG, "stopLogging")
        tracker?.unsetEventListener()
    }

}