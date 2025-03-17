package kaist.iclab.tracker.sensor.phone

import android.Manifest
import android.content.Context
import android.content.pm.ServiceInfo
import android.os.Build
import kaist.iclab.tracker.listener.AccessibilityListener
import kaist.iclab.tracker.listener.core.AccessibilityEventInfo
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import java.lang.ref.WeakReference

class UserInteractionSensor(
    val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    val stateStorage: StateStorage<SensorState>,
) : BaseSensor<UserInteractionSensor.Config, UserInteractionSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    companion object {
        var instance: WeakReference<UserInteractionSensor>? = null
    }

    init {
        instance = WeakReference(this)
    }

    /*No attribute required... can not be data class*/
    class Config: SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val packageName: String,
        val className: String,
        val eventType: Int,
        val text: String
    ) : SensorEntity

    override val permissions = listOfNotNull(
        Manifest.permission.BIND_ACCESSIBILITY_SERVICE
    ).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else null
    ).toTypedArray()

    override val defaultConfig = Config()

    private val accessibilityListener = AccessibilityListener()

    private val mainCallback = mainCallback@{ e: AccessibilityEventInfo ->
        if(e is AccessibilityEventInfo.Interrupt) return@mainCallback // Ignore interrupts
        val event = e as AccessibilityEventInfo.Event

        listeners.forEach { listener ->
            val timestamp = System.currentTimeMillis()

            event.event?.let {
                listener.invoke(
                    Entity(
                        timestamp,
                        timestamp * 1000 * 1000, // In nanoseconds
                        it.packageName?.toString()?: "UNKNOWN",
                        it.className?.toString()?: "UNKNOWN",
                        it.eventType,
                        it.text.toString()
                    )
                )
            }
        }
    }

    override fun init() {
        stateStorage.set(SensorState(SensorState.FLAG.DISABLED))
    }

    override fun onStart() {
        accessibilityListener.addListener(mainCallback)
    }

    override fun onStop() {
        accessibilityListener.removeListener(mainCallback)
    }
}