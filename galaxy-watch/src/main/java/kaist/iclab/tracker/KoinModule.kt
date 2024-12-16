package kaist.iclab.tracker

import kaist.iclab.tracker.controller.CollectorControllerInterface
import kaist.iclab.tracker.permission.PermissionManagerInterface
import org.koin.dsl.module

val koinModule = module {
    single<CollectorControllerInterface> {
        Tracker.getCollectorController()
    }
    single<PermissionManagerInterface> {
        Tracker.getPermissionManager()
    }

}