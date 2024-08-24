package dev.iclab.tracker

import dev.iclab.tracker.collectors.BatteryCollector
import dev.iclab.tracker.collectors.LocationCollector
import dev.iclab.tracker.collectors.TestCollector
import dev.iclab.tracker.collectors.controller.CollectorControllerInterface
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.permission.PermissionManagerInterface
import dev.iclab.tracker.ui.AbstractMainViewModel
import dev.iclab.tracker.ui.MainViewModelImpl
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
