package kaist.iclab.lab_galaxywatch_tracker.data.trigger

import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker

abstract class HealthTrackerTrigger : HealthTracker.TrackerEventListener {
    val TAG = javaClass.simpleName

    override fun onError(trackerError: HealthTracker.TrackerError) {
        Log.d(TAG, "onError")
        when (trackerError) {
            HealthTracker.TrackerError.PERMISSION_ERROR -> Log.e(
                TAG,
                "ERROR: Permission Failed"
            )

            HealthTracker.TrackerError.SDK_POLICY_ERROR -> Log.e(
                TAG,
                "ERROR: SDK Policy Error"
            )

            else -> Log.e(TAG, "ERROR: Unknown ${trackerError.name}")
        }
    }

    override fun onFlushCompleted() {
        Log.d(TAG, "onFlushCompleted")
    }
}