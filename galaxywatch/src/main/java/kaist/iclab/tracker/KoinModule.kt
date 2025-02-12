package kaist.iclab.tracker

import kaist.iclab.tracker.controller.CollectorController
import kaist.iclab.tracker.permission.PermissionManager
import org.koin.dsl.module

val koinModule = module {
    single<CollectorController> {
        Tracker.getCollectorController()
    }
    single<PermissionManager> {
        Tracker.getPermissionManager()
    }

}