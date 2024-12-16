package kaist.iclab.tracker.collectors.core

import kaist.iclab.tracker.CollectorUtil

open class DataEntity(
    open val received: Long,
    open val deviceId: String = CollectorUtil.getDeviceId(),
    open val deviceModel: String = CollectorUtil.getDeviceModel(),
    open val app: String = CollectorUtil.getApp() + ":" + CollectorUtil.getAppVersion(),
    open val androidVersion: String = CollectorUtil.getOSVersion()
)