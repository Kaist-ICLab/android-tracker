package dev.iclab.tracker.collectors

import android.content.Context
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.filters.Filter

abstract class AbstractCollector(
    open val context: Context,
    open val database: DatabaseInterface
) {
    open val permissions: Array<String> = arrayOf()
    open val filters: MutableList<Filter> = mutableListOf()

    /* Check whether the system allow to collect data
    * In case of sensor malfunction or broken, it would not be available.*/
    abstract fun isAvailable(): Boolean

    /* Enable the collector by checking and requesting permissions
    * Different with `isAvailable`, `enable` is used to request permissions when
    * the collector is available, but does not have permission
    * */
    abstract suspend fun enable(): Boolean

    /* Start collector to collect data
    * */
    abstract fun start()

    /* Stop collector to stop collecting data
    * */
    abstract fun stop()

    /* Flush data*/
    abstract fun flush()
}