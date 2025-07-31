package kaist.iclab.tracker.sensor.phone

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.TrafficStats
import android.os.Build
import kaist.iclab.tracker.listener.AlarmListener
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage

class DataTrafficStatSensor(
    context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>,
) : BaseSensor<DataTrafficStatSensor.Config, DataTrafficStatSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    data class Config(
        val interval: Long,
    ) : SensorConfig

    data class Entity(
        val received: Long,
        val timestamp: Long,
        val totalRx: Long,
        val totalTx: Long,
        val mobileRx: Long,
        val mobileTx: Long,
    ) : SensorEntity

    override val permissions = listOfNotNull(
        Manifest.permission.PACKAGE_USAGE_STATS,
        Manifest.permission.READ_PHONE_STATE,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) Manifest.permission.FOREGROUND_SERVICE else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) Manifest.permission.FOREGROUND_SERVICE_CONNECTED_DEVICE else null,
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) Manifest.permission.FOREGROUND_SERVICE_DATA_SYNC else null
    ).toTypedArray()

    override val foregroundServiceTypes: Array<Int> = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)  listOfNotNull<Int>(
        ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE,
        ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
    ).toTypedArray() else emptyArray()

    private val actionName = "kaist.iclab.tracker.ACTION_DATA_TRAFFIC_STAT"
    private val actionCode = 0x11
    private val alarmListener = AlarmListener(context, actionName, actionCode, configStateFlow.value.interval)
    private val mainCallback = { _: Intent? ->
        val timestamp = System.currentTimeMillis()
        listeners.forEach { listener ->
            listener.invoke(
                Entity(
                    timestamp,
                    timestamp,
                    TrafficStats.getTotalRxBytes(),
                    TrafficStats.getTotalTxBytes(),
                    TrafficStats.getMobileRxBytes(),
                    TrafficStats.getMobileTxBytes(),
                )
            )
        }
    }

    override fun onStart() {
        alarmListener.addListener(mainCallback)
    }

    override fun onStop() {
        alarmListener.removeListener(mainCallback)
    }
}