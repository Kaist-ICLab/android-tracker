package kaist.iclab.wearablelogger.collector

interface AbstractCollector {
    val TAG: String
    fun setup()
    fun startLogging()
    fun stopLogging()
    fun flush()
}