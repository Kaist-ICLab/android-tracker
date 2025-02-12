package kaist.iclab.tracker.collector.phone

//class WifiScanCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface
//) : AbstractCollector<WifiScanCollector.Config, WifiScanCollector.Entity>(permissionManager) {
//    override val permissions = listOfNotNull(
//        Manifest.permission.ACCESS_WIFI_STATE,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    class Config() : CollectorConfig()
//
//    override val _defaultConfig = Config()
//
//    override fun start() {
//        super.start()
//        broadcastTrigger.register()
//    }
//
//    override fun stop() {
//        broadcastTrigger.unregister()
//        super.stop()
//    }
//
//    override fun isAvailable(): Availability {
//        return  Availability(wifiManager.isWifiEnabled, if(!wifiManager.isWifiEnabled) "WiFi is disabled" else null)
//    }
//
//    private val wifiManager: WifiManager by lazy {
//        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
//    }
//
//    private val broadcastTrigger = BroadcastListener(
//        context,
//        arrayOf(
//            WifiManager.SCAN_RESULTS_AVAILABLE_ACTION
//        )
//    ) {
//        val results = wifiManager.scanResults
//        val timestamp = System.currentTimeMillis()
//        results.forEach {
//            listener?.invoke(
//                Entity(
//                    timestamp,
//                    timestamp,
//                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
//                    it.wifiSsid?.toString() ?: "UNKNOWN"
//                    else it.SSID,
//                    it.BSSID,
//                    it.frequency,
//                    it.level,
//                )
//            )
//        }
//    }
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val ssid: String,
//        val bssid: String,
//        val frequency: Int,
//        val level: Int
//    ) : DataEntity(received)
//}