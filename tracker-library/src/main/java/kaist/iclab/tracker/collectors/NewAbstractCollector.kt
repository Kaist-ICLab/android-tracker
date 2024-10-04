package kaist.iclab.tracker.collectors

import android.content.Context
import android.util.Log
import kaist.iclab.tracker.permission.PermissionManagerInterface

abstract class NewAbstractCollector(
    open val context: Context
) {
    abstract val permissions: Array<String>
    open val TAG: String = this::class.simpleName ?: "UnnamedClass"
    open val NAME: String = extractName(this::class.simpleName ?: "UnknownCollector")

    var listener: ((DataEntity)-> Unit)? = null

    abstract class Config
    abstract class DataEntity

    /* Check whether the system allow to collect data
    * In case of sensor malfunction or broken, it would not be available.*/
    abstract fun isAvailable(): Boolean

    /* Enable the collector by checking and requesting permissions
    * Different with `isAvailable`, `enable` is used to request permissions when
    * the collector is available, but does not have permission
    * */
    fun enable(permissionManager: PermissionManagerInterface, onResult: (granted: Boolean)-> Unit) {
        permissionManager.request(permissions){
            Log.d(TAG, "Permission granted: ${permissions.all { permission -> it[permission] == true }}")
            onResult(permissions.all { permission -> it[permission] == true })
        }
    }


    /* Start collector to collect data
    * */
    abstract fun start()

    /* Stop collector to stop collecting data
    * */
    abstract fun stop()


    private fun extractName(className: String): String {
        // Replace "Collector" with an empty string
        val nameWithoutCollector = className.replace("Collector", "")

        // Split the name into parts based on camel case and underscores
        val parts = nameWithoutCollector.split("(?=\\p{Upper})|_|(?<=\\p{Lower})(?=\\p{Upper})".toRegex())

        // Join the parts and convert to uppercase
        return parts.joinToString("_").uppercase()
    }

}