package kaist.iclab.tracker.collector.phone

import android.Manifest
import android.content.Context
import android.util.Log
import kaist.iclab.tracker.collector.core.AbstractCollector
import kaist.iclab.tracker.collector.core.Availability
import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.collector.core.DataEntity
import kaist.iclab.tracker.data.core.SingletonStorageInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit
import kotlin.reflect.KClass

class SampleCollector(
    val context: Context,
    permissionManager: PermissionManagerInterface,
    configStorage: SingletonStorageInterface<Config>,
    stateStorage: SingletonStorageInterface<CollectorState>,
) : AbstractCollector<SampleCollector.Config, SampleCollector.Entity>(permissionManager
    , configStorage, stateStorage) {

    companion object{
        val defaultConfig = Config(
            TimeUnit.SECONDS.toMillis(5)
        )
    }
    override val _defaultConfig = defaultConfig

    override val permissions = listOfNotNull<String>(
        Manifest.permission.BODY_SENSORS
    ).toTypedArray()
    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()

    data class Config(
        val interval: Long,
    ) : CollectorConfig


    override fun getConfigClass(): KClass<out CollectorConfig> {
        return Config::class
    }

    override fun getEntityClass(): KClass<out DataEntity> {
        return Entity::class
    }

    private var job: Job? = null

    override fun start() {
        super.start()
        job = CoroutineScope(Dispatchers.IO).launch {
            while(isActive){
                val timestamp = System.currentTimeMillis()
                Log.d("TAG", "SampleCollector: $timestamp")
                listener?.invoke(Entity(
                    timestamp,
                    timestamp
                ))
                sleep(configFlow.value.interval)
            }
        }

    }

    override fun stop() {
        Log.d(NAME, "STOP()")
        job?.cancel()
        job = null
        super.stop()
    }

    override fun isAvailable() = Availability(true)


    data class Entity(
        override val received: Long,
        val timestamp: Long,
    ) : DataEntity(received)
}