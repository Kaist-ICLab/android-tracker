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

    fun createUserReportNotfChannel(context: Context)
    fun showUserReportNotf(context: Context, title: String, text: String, intent: Intent)

}