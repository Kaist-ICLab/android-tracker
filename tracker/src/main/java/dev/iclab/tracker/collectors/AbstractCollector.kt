package dev.iclab.tracker.collectors

abstract class AbstractCollector() {
    open val permissions: Array<String> = arrayOf()

    /* Check whether the system allow to collect data*/
    open fun isAvailable(): Boolean {
        return true
    }

    /* Enable the collector by checking and requesting permissions
    * */
    open suspend fun enable(): Boolean{
        return true
    }

    /* Start collector to collect data
    * */
    open fun start() {

    }

    /* Stop collector to stop collecting data
    * */
    open fun stop() {

    }

    /* Flush data*/
    open fun flush() {

    }
}