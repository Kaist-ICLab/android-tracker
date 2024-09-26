package kaist.iclab.lab_galaxywatch_tracker

import kaist.iclab.tracker.Tracker
import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.database.DatabaseInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import kaist.iclab.lab_galaxywatch_tracker.data.collector.ACCCollector
import kaist.iclab.lab_galaxywatch_tracker.data.collector.HRCollector
import kaist.iclab.lab_galaxywatch_tracker.data.collector.PPGGreenCollector
import kaist.iclab.lab_galaxywatch_tracker.data.collector.SkinTempCollector
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