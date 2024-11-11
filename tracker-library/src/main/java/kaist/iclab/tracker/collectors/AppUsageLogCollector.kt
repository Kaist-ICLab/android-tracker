package kaist.iclab.tracker.collectors

import android.Manifest
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build
import kaist.iclab.tracker.controller.AbstractCollector
import kaist.iclab.tracker.controller.Availability
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.DataEntity
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.triggers.AlarmTrigger
import java.util.concurrent.TimeUnit

class AppUsageLogCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface
) : AbstractCollector<AppUsageLogCollector.Config, AppUsageLogCollector.Entity>(permissionManager) {
    override val permissions = listOfNotNull<String>(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) Manifest.permission.PACKAGE_USAGE_STATS else null,
    ).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    data class Config(
        val interval: Long,
    ) : CollectorConfig()

    override val defaultConfig = Config(
        TimeUnit.MINUTES.toMillis(1)
    )

    override fun start() {
        super.start()
        trigger.register()
    }

    override fun stop() {
        trigger.unregister()
        super.stop()
    }

    override fun isAvailable() = Availability(true)

    val ACTION = "kaist.iclab.tracker.${NAME}_REQUEST"
    val CODE = 0x11
    val trigger: AlarmTrigger = AlarmTrigger(
        context, ACTION, CODE,
        defaultConfig.interval
    ) {
        val usageStatManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val timestamp = System.currentTimeMillis()
        /*Give margin for alarm amy not correctly given*/
        val events = usageStatManager.queryEvents(
            timestamp - configFlow.value.interval - TimeUnit.MINUTES.toMillis(5),
            timestamp
        )
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            listener?.invoke(
                Entity(
                    timestamp,
                    event.timeStamp,
                    event.packageName,
                    isPreinstalledApp(event.packageName),
                    event.eventType
                )
            )
        }

    }

    private fun isPreinstalledApp(packageName: String): String {
        val packageManager = context.packageManager
        try{
            packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA).let {
                if(it.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0){
                    return "SYSTEM"
                }else{
                    return "USER"
                }
            }
        } catch (e: PackageManager.NameNotFoundException) {
            return "UNKNOWN"
        }

    }

    data class Entity(
        override val received: Long,
        val timestamp: Long,
        val packageName: String,
        val installedBy: String,
        val eventType: Int
    ) : DataEntity(received)
}