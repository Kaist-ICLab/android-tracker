package kaist.iclab.lab_galaxywatch_tracker.data.collector

import android.Manifest
import android.content.Context
import android.os.Build
import com.samsung.android.service.health.tracking.HealthTracker
import kaist.iclab.tracker.collectors.AbstractCollector
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.lab_galaxywatch_tracker.data.source.HealthTrackerSource

abstract class WearableSensorCollector(
    context: Context,
    database: DatabaseInterface,
    private val healthTrackerSource: HealthTrackerSource
) : AbstractCollector(context, database) {
    override val permissions: Array<String> = listOfNotNull(
        Manifest.permission.BODY_SENSORS,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        Manifest.permission.ACTIVITY_RECOGNITION
    ).toTypedArray()

    abstract val tracker: HealthTracker
    abstract val trigger: HealthTracker.TrackerEventListener

    /*TODO: Change it to override after changing library*/
    fun enable_(
        permissionManager: PermissionManagerInterface,
        onResult: (granted: Boolean) -> Unit
    ) {
        super.enable(permissionManager, onResult)
    }

    override fun isAvailable(): Boolean {
        /*TODO: Check Whether Google GMS Service is available*/
        /*TODO: Check whether */
        return true
    }

    override fun start() {
        tracker.setEventListener(trigger)
    }

    override fun stop() {
        tracker.unsetEventListener()
    }
}