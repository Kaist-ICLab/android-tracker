package kaist.iclab.tracker.data

import kaist.iclab.tracker.collectors.core.CollectorConfig
import kaist.iclab.tracker.collectors.core.CollectorState

data class StateConfigResponse(
    val state: CollectorState,
    val config: CollectorConfig
)
