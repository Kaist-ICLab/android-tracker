package kaist.iclab.tracker.controller

import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.collector.core.Collector
import kaist.iclab.tracker.data.core.StateStorage
import kotlinx.coroutines.flow.StateFlow

interface CollectorController {
    val trackerStateFlow: StateFlow<TrackerState>
    fun init(
        collectorMap: Map<String, Collector>,
        stateStorage: StateStorage<TrackerState>
    )
    fun start()
    fun stop()
}