package kaist.iclab.wearablelogger.data

import android.Manifest
import android.content.Context
import android.os.Build
import com.samsung.android.service.health.tracking.HealthTracker
import dev.iclab.tracker.collectors.AbstractCollector
import dev.iclab.tracker.database.DatabaseInterface

abstract class WearableSensorCollector(
    context: Context,
    database: DatabaseInterface
): AbstractCollector(context, database) {
    override val permissions: Array<String> = listOfNotNull(
        Manifest.permission.BODY_SENSORS,
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.BODY_SENSORS_BACKGROUND else null,
        Manifest.permission.ACTIVITY_RECOGNITION
    ).toTypedArray()

    abstract var tracker: HealthTracker?

}