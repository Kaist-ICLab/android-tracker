package kaist.iclab.tracker.sensor.phone

import android.Manifest
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import kaist.iclab.tracker.listener.AlarmListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.util.concurrent.TimeUnit

class AppUsageLogSensor(
    val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    stateStorage: StateStorage<SensorState>,
) : BaseSensor<AppUsageLogSensor.Config, AppUsageLogSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    data class Config(
        val interval: Long,
    ) : SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val packageName: String,
        val installedBy: String,
        val eventType: Int
    ) : SensorEntity

    override val defaultConfig = Config(
        TimeUnit.MINUTES.toMillis(30)
    )

    override val permissions = listOfNotNull(Manifest.permission.PACKAGE_USAGE_STATS).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    val actionName = "kaist.iclab.tracker.${NAME}_REQUEST"
    private val actionCode = 0x11
    private val alarmListener: AlarmListener = AlarmListener(
        context = context,
        actionName = actionName,
        actionCode = actionCode,
        defaultConfig.interval
    )

    private val mainCallback = { _: Intent? ->
        val usageStatManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val timestamp = System.currentTimeMillis()
        /*Give margin for alarm amy not correctly given*/
        val events = usageStatManager.queryEvents(
            timestamp - configStateFlow.value.interval - TimeUnit.MINUTES.toMillis(5),
            timestamp
        )
        val event = UsageEvents.Event()
        while (events.hasNextEvent()) {
            events.getNextEvent(event)
            listeners.forEach { listener ->
                listener.invoke(
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

    }

    override fun init() {
        Log.v("AmbientLightCollector", "Is init ever called?")
    }

    override fun onStart() {
        permissionManager.request(permissions)
        alarmListener.addListener(mainCallback)
    }

    override fun onStop() {
        alarmListener.removeListener(mainCallback)
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
}