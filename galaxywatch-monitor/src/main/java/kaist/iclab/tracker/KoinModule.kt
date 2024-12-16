package kaist.iclab.tracker

import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.tracker.data.WearableDataCollector
import kaist.iclab.tracker.ui.AbstractMainViewModel
import kaist.iclab.tracker.ui.MainViewModelImpl
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