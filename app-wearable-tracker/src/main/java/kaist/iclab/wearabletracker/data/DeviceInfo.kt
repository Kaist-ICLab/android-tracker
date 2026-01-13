package kaist.iclab.wearabletracker.data

import kaist.iclab.wearabletracker.Constants

data class DeviceInfo(
    val name: String = Constants.DeviceInfo.UNKNOWN_NAME,
    val id: String = Constants.DeviceInfo.UNKNOWN_ID
)
