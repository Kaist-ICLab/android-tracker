package kaist.iclab.field_tracker.ui

import androidx.lifecycle.ViewModel
import kaist.iclab.tracker.controller.CollectorConfig
import kaist.iclab.tracker.controller.CollectorInterface
import kaist.iclab.tracker.controller.CollectorState
import kotlinx.coroutines.flow.StateFlow

abstract class AbstractMainViewModel: ViewModel(), MainViewModelInterface
