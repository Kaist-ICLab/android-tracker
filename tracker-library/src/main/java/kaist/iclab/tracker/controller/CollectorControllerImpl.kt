package kaist.iclab.tracker.controller

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.collector.core.CollectorInterface
import kaist.iclab.tracker.collector.core.CollectorState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class CollectorControllerImpl(
    private val context: Context,
) : CollectorControllerInterface {
    private val _stateFlow = MutableStateFlow(
        TrackerState(TrackerState.FLAG.DISABLED, "Tracker is uninitialized")
    )
    override val stateFlow: StateFlow<TrackerState>
        get() = _stateFlow.asStateFlow()

    private var _collectorMap: Map<String, CollectorInterface> = emptyMap()
    override fun initializeCollectors(collectorMap: Map<String, CollectorInterface>) {
        _collectorMap = collectorMap
        _collectorMap.forEach { (_, collector) ->
            collector.initialize()
        }
        _stateFlow.value = TrackerState(TrackerState.FLAG.READY)
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
            Log.d("CollectorService", "Notification Post was called")
            controller._stateFlow.value = TrackerState(TrackerState.FLAG.RUNNING)
            collectorMap.filter{ (_, collector) ->
                collector.stateFlow.value.flag == CollectorState.FLAG.ENABLED
            }.forEach { (_, collector) ->
                collector.start()
            }
        }

        fun stop() {
            controller._stateFlow.value = TrackerState(TrackerState.FLAG.READY)
            collectorMap.filter{(_, collector) ->
                collector.stateFlow.value.flag == CollectorState.FLAG.RUNNING
            }.forEach { (_, collector) ->
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
            collectorMap.filter{(_, collector)->
                collector.stateFlow.value.flag == CollectorState.FLAG.ENABLED
            }.forEach { (_, collector) ->
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