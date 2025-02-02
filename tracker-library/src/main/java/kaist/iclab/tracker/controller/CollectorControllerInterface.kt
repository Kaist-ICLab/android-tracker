package kaist.iclab.tracker.controller

import kaist.iclab.tracker.TrackerState
import kaist.iclab.tracker.collector.core.CollectorInterface
import kotlinx.coroutines.flow.StateFlow

interface CollectorControllerInterface {
    val stateFlow: StateFlow<TrackerState>
    fun initializeCollectors(
        collectorMap: Map<String, CollectorInterface>
    )
    fun start()
    fun stop()
}