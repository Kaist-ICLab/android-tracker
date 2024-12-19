package kaist.iclab.tracker.data

import kaist.iclab.tracker.collector.core.CollectorConfig
import kaist.iclab.tracker.collector.core.CollectorState

data class StateConfigResponse(
    val state: CollectorState,
    val config: CollectorConfig
)
