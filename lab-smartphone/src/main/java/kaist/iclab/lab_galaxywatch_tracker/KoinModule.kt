package kaist.iclab.lab_galaxywatch_tracker

import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.collectors.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.lab_galaxywatch_tracker.data.WearableDataCollector
import kaist.iclab.lab_galaxywatch_tracker.ui.AbstractMainViewModel
import kaist.iclab.lab_galaxywatch_tracker.ui.MainViewModelImpl
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val koinModule = module {
    single<DatabaseInterface> {
        Tracker.getDatabase()
    }
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }

    singleOf(::WearableDataCollector)

    viewModelOf(::MainViewModelImpl)
    viewModel<AbstractMainViewModel>{
        get<MainViewModelImpl>()
    }
}