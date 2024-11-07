package kaist.iclab.tracker.controller

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import kaist.iclab.tracker.Tracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge

class CollectorControllerImpl(
    private val context: Context,
) : CollectorControllerInterface {
    private val _stateFlow = MutableStateFlow<Boolean>(false)
    override val stateFlow: StateFlow<Boolean>
        get() = _stateFlow.asStateFlow()

    override fun collectorStateFlow(): Flow<Map<String, CollectorState>> {
        return combine(_collectorMap.map { (key, collector) ->
            collector.stateFlow.map { state ->
                key to state
            }
        }) { pairs -> pairs.toMap() }
    }

    private var _collectorMap: Map<String, CollectorInterface> = emptyMap()
    override fun initializeCollectors(collectorMap: Map<String, CollectorInterface>) {
        _collectorMap = collectorMap
        _collectorMap.forEach { (_, collector) ->
            collector.initialize()
        }
    }

    override fun enableCollector(name: String) {
        Log.d("CollectorControllerImpl", "enableCollector: $name")
        _collectorMap[name]?.let { collector ->
            Log.d("CollectorControllerImpl", "Collector found")
            collector.requestPermissions() {
                if (it) {
                    collector.enable()
                }
            }
        }
    }

    override fun disableCollector(name: String) {
        _collectorMap[name]?.disable()
    }

    override fun configFlow(): Flow<Map<String, CollectorConfig>> {
        return combine(_collectorMap.map { (key, collector) ->
            collector.configFlow.map { config ->
                key to config
            }
        }) { pairs -> pairs.toMap() }
    }

    override fun updateConfig(config: Map<String, CollectorConfig>) {
        _collectorMap.forEach { (name, collector) ->
            config[name]?.let {
                collector.updateConfig(it)
            }
        }
    }

    private val serviceIntent = Intent(context, CollectorService::class.java)

    override fun start() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }

    override fun stop() {
        context.stopService(serviceIntent)
    }

    class CollectorService : Service() {
        val controller = Tracker.getCollectorController() as CollectorControllerImpl
        val notfManager = Tracker.getNotfManager()
        val collectorMap = controller._collectorMap
        override fun onBind(intent: Intent?): IBinder? = null
        override fun onDestroy() {
            stop()
        }

        fun run() {
            notfManager.startForegroundService(
                this,
                requiredForegroundServiceType()
            )
            controller._stateFlow.tryEmit(true)
            collectorMap.forEach { (_, collector) ->
                collector.start()
            }
        }

        fun stop() {
            controller._stateFlow.tryEmit(false)
            collectorMap.forEach { (_, collector) ->
                collector.stop()
            }
            stopSelf()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                stopForeground(STOP_FOREGROUND_REMOVE)
            } else {
                stopForeground(true)
            }
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            try {
                run()
            } catch (e: Exception) {
                Log.e("CollectorService", "ERROR:${e}")
                stop()
            }
            return super.onStartCommand(intent, flags, startId)
        }

        fun requiredForegroundServiceType(): Int {
            val serviceTypes = mutableSetOf<Int>()
            collectorMap.forEach { (_, collector) ->
                serviceTypes.addAll(collector.foregroundServiceTypes)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                serviceTypes.add(ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
            }
            return if (serviceTypes.isNotEmpty()) {
                serviceTypes.reduce { acc, type -> acc or type }
            } else {
                0
            }
        }
    }
}