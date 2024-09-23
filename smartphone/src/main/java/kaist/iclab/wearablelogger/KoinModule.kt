package kaist.iclab.wearablelogger

import dev.iclab.tracker.Tracker
import dev.iclab.tracker.collectors.controller.CollectorControllerInterface
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.wearablelogger.data.WearableDataCollector
import kaist.iclab.wearablelogger.ui.AbstractMainViewModel
import kaist.iclab.wearablelogger.ui.MainViewModelImpl
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