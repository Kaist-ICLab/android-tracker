package kaist.iclab.wearablelogger.collector

interface AbstractCollector {
    val TAG: String
    fun setup()
    fun startLogging()
    fun stopLogging()
    fun zip2prepareSend(): ArrayList<String>
    fun flush()
}