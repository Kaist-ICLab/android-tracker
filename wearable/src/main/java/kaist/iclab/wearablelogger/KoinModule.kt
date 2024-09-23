package kaist.iclab.wearablelogger

import dev.iclab.tracker.Tracker
import dev.iclab.tracker.collectors.controller.CollectorControllerInterface
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.wearablelogger.data.collector.ACCCollector
import kaist.iclab.wearablelogger.data.collector.HRCollector
import kaist.iclab.wearablelogger.data.collector.PPGGreenCollector
import kaist.iclab.wearablelogger.data.collector.SkinTempCollector
import org.koin.core.module.dsl.singleOf
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

    singleOf(::PPGGreenCollector)
    singleOf(::ACCCollector)
    singleOf(::SkinTempCollector)
    singleOf(::HRCollector)
}