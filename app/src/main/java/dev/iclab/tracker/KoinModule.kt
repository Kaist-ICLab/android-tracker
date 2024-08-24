package dev.iclab.tracker

import dev.iclab.tracker.collectors.BatteryCollector
import dev.iclab.tracker.collectors.LocationCollector
import dev.iclab.tracker.collectors.TestCollector
import dev.iclab.tracker.collectors.controller.CollectorControllerInterface
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.permission.PermissionManagerInterface
import dev.iclab.tracker.ui.MainViewModel
import org.koin.core.module.dsl.viewModel
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

    single<TestCollector> {
        TestCollector(get(), get())
    }

    single<BatteryCollector> {
        BatteryCollector(get(), get())
    }

    single<LocationCollector> {
        LocationCollector(get(), get())
    }
    viewModel { MainViewModel(get(), get(), get()) }
}