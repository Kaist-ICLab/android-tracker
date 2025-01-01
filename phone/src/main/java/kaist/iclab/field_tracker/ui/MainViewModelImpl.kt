package kaist.iclab.field_tracker.ui

import kaist.iclab.tracker.TrackerUtil
import kaist.iclab.tracker.controller.CollectorControllerInterface


class MainViewModelImpl(
    private val collectorController: CollectorControllerInterface,
    override val collectors: Array<String>
): AbstractMainViewModel() {
    companion object {
        const val TAG = "MainViewModelImpl"
    }

    override val controllerStateFlow = collectorController.stateFlow
    override val collectorStateFlow = collectorController.collectorStateFlow()
    override val configFlow = collectorController.configFlow()

    override fun start() {
        collectorController.start()
    }

    override fun stop() {
        collectorController.stop()
    }

    override fun enableCollector(name: String) {
        collectorController.enableCollector(name)
    }

    override fun disableCollector(name: String) {
        collectorController.disableCollector(name)
    }

    override fun getDeviceInfo(): String {
        return TrackerUtil.getDeviceModel()
    }

    override fun getAppVersion(): String {
        return TrackerUtil.getAppVersion()
    }

    //    override fun sync() {
//        throw NotImplementedError("Not implemented")
//    }
//
//    override fun delete() {
//        throw NotImplementedError("Not implemented")
//    }
}