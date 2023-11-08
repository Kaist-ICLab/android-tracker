package kaist.iclab.wearablelogger.collector

abstract class AbstractCollector {

    abstract fun setup()
    abstract fun startLogging()
    abstract fun stopLogging()
}