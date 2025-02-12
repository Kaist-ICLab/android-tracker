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
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.collector.core.CollectorState
import kaist.iclab.tracker.data.core.StateStorage
import kotlinx.coroutines.flow.StateFlow

class CollectorControllerImpl(
    private val context: Context
) : CollectorController {
    private lateinit var stateStorage: StateStorage<TrackerState>
    override val trackerStateFlow: StateFlow<TrackerState>
        get() = stateStorage.stateFlow

    private var _collectorMap: Map<String, Collector> = emptyMap()
    override fun init(collectorMap: Map<String, Collector>, stateStorage: StateStorage<TrackerState>) {
        this.stateStorage = stateStorage
        _collectorMap = collectorMap
        _collectorMap.forEach { (_, collector) ->
            collector.init()
        }
    }

    private val serviceIntent = Intent(context, CollectorService::class.java)

    override fun start() {
        context.startForegroundService(serviceIntent)
    }

    override fun stop() {
        context.stopService(serviceIntent)
    }

    class CollectorService : Service() {
        private val controller = Tracker.getCollectorController() as CollectorControllerImpl
        private val notfManager = Tracker.getNotfManager()
        private val collectorMap = controller._collectorMap
        override fun onBind(intent: Intent?): IBinder? = null
        override fun onDestroy() {
            stop()
        }

        private fun run() {
            notfManager.postForegroundService(
                this,
                requiredForegroundServiceType()
            )
            Log.d("CollectorService", "Notification Post was called")
            controller.stateStorage.set(TrackerState(TrackerState.FLAG.RUNNING))
            collectorMap.forEach() { (_, collector) ->
                if(collector.collectorStateFlow.value.flag == CollectorState.FLAG.ENABLED){
                    collector.start()
                }
            }
        }

        private fun stop() {
            controller.stateStorage.set(TrackerState(TrackerState.FLAG.READY))
            collectorMap.forEach { (_, collector) ->
                if(collector.collectorStateFlow.value.flag == CollectorState.FLAG.RUNNING){
                    collector.stop()
                }
            }
            stopSelf()
            stopForeground(STOP_FOREGROUND_REMOVE)
        }

        override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
            try {
                run()
            } catch (e: Exception) {
                stop()
            }
            return super.onStartCommand(intent, flags, startId)
        }

        private fun requiredForegroundServiceType(): Int {
            val serviceTypes = collectorMap.map { (_, collector) ->
                if(collector.collectorStateFlow.value.flag == CollectorState.FLAG.ENABLED){
                    collector.foregroundServiceTypes.toList()
                }else{
                    emptyList()
                }
            }.flatten().toMutableSet()
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