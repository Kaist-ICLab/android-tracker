package kaist.iclab.tracker.notf

import android.app.Service
import android.content.Context

interface NotfManagerInterface {

    fun setServiceNotfDescription(
        title: String? = null,
        description: String? = null,
        icon: Int
    )
    fun createServiceNotfChannel(context: Context)
    fun startForegroundService(service: Service, foregroundTypes: Int)

}