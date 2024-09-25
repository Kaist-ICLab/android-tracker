package kaist.iclab.field_tracker

import kaist.iclab.tracker.collectors.BatteryCollector
import kaist.iclab.tracker.collectors.LocationCollector
import kaist.iclab.tracker.collectors.TestCollector
import kaist.iclab.tracker.collectors.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.field_tracker.ui.AbstractMainViewModel
import kaist.iclab.field_tracker.ui.MainViewModelImpl
import kaist.iclab.tracker.Tracker
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module


val appModule = module {
    single<DatabaseInterface> {
        Tracker.getDatabase()
    }
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }
    singleOf(::TestCollector)
    singleOf(::BatteryCollector)
    singleOf(::LocationCollector)

    viewModelOf(::MainViewModelImpl)
    viewModel<AbstractMainViewModel>{
        get<MainViewModelImpl>()
    }
}
