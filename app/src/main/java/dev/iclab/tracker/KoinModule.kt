package dev.iclab.tracker

import dev.iclab.tracker.collectors.BatteryCollector
import dev.iclab.tracker.collectors.TestCollector
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.ui.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single<DatabaseInterface> {
        TrackerService.getDatabase()
    }
    single<CollectorController> {
        TrackerService.getCollectorController()
    }

    single<TestCollector> {
        TestCollector(get(), get())
    }

    single<BatteryCollector> {
        BatteryCollector(get(), get())
    }
    viewModel { MainViewModel(get(), get()) }
}