package dev.iclab.tracker.collectors

import android.content.Context
import dev.iclab.tracker.PermissionManager
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.filters.Filter

abstract class AbstractCollector(
    open val context: Context,
    open val database: DatabaseInterface
) {
    abstract val NAME: String
    abstract val permissions: Array<String>
    open val filters: MutableList<Filter> = mutableListOf()

    open val TAG: String = this::class.simpleName ?: "UnnamedClass"

    /* Check whether the system allow to collect data
    * In case of sensor malfunction or broken, it would not be available.*/
    abstract fun isAvailable(): Boolean

    /* Enable the collector by checking and requesting permissions
    * Different with `isAvailable`, `enable` is used to request permissions when
    * the collector is available, but does not have permission
    * */
    fun enable(permissionManager: PermissionManager, onResult: (granted: Boolean)-> Unit) {
        permissionManager.request(permissions){
            onResult(permissions.all { permission -> it[permission] == true })
        }
    }

    /* Start collector to collect data
    * */
    abstract fun start()

    /* Stop collector to stop collecting data
    * */
    abstract fun stop()

}