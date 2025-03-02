package kaist.iclab.tracker.sensor.phone

//class BluetoothScanCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface
//) : AbstractCollector<BluetoothScanCollector.Config, BluetoothScanCollector.Entity>(
//    permissionManager
//) {
//
//    override val permissions = listOfNotNull(
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_CONNECT else null,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) Manifest.permission.BLUETOOTH_SCAN else null,
//        Manifest.permission.BLUETOOTH,
//        Manifest.permission.BLUETOOTH_ADMIN,
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION,
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) Manifest.permission.ACCESS_BACKGROUND_LOCATION else null
//    ).toTypedArray()
//
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    data class Config(
//        val doScan: Boolean,
//        val interval: Long,
//        val scanDuration: Long,
//    ) : CollectorConfig()
//
//    override val _defaultConfig = Config(
//        true,
//        TimeUnit.MINUTES.toMillis(3),
//        TimeUnit.SECONDS.toMillis(15)
//    )
//
//    override fun isAvailable(): Availability {
//        if (bluetoothManager.adapter.isEnabled) {
//            return Availability(true, null)
//        } else {
//            return Availability(false, "Bluetooth is disabled")
//        }
//    }
//
//    override fun start() {
//        super.start()
//        trigger.register()
//        if (configFlow.value.doScan) {
//            alarmListener.register()
//        }
//    }
//
//    override fun stop() {
//        trigger.unregister()
//        if (configFlow.value.doScan) {
//            alarmListener.unregister()
//        }
//        super.stop()
//    }
//
//    private fun handleDeviceFound(
//        device: BluetoothDevice,
//        timestamp: Long,
//        rssi: Int,
//        isLE: Boolean
//    ) {
//        listener?.invoke(
//            Entity(
//                System.currentTimeMillis(),
//                timestamp,
//                device.name ?: "UNKNOWN",
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) device.alias
//                    ?: "UNKNOWN" else "UNKNOWN",
//                device.address,
//                device.bondState,
//                device.type,
//                device.bluetoothClass.deviceClass,
//                rssi,
//                isLE
//            )
//        )
//    }
//
//    val trigger = BroadcastListener(
//        context,
//        arrayOf(
//            BluetoothDevice.ACTION_FOUND
//        )
//    ) { intent ->
//        val extras = intent.extras ?: return@BroadcastListener
//        val rssi = extras.getShort(BluetoothDevice.EXTRA_RSSI, 0).toInt()
//        val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            extras.getParcelable(BluetoothDevice.EXTRA_DEVICE, BluetoothDevice::class.java)
//        } else {
//            extras.getParcelable<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice
//        } ?: return@BroadcastListener
//        handleDeviceFound(device, System.currentTimeMillis(), rssi, false)
//    }
//
//    val alarmListener = AlarmListener(
//        context, "BT_SCAN_REQUEST", 0x00, configFlow.value.interval,
//    ) {
//        val adapter = bluetoothManager.adapter
//        if (adapter.isEnabled) {
//            adapter.startDiscovery()
//            adapter.bluetoothLeScanner.startScan(scanCallback)
//
//            CoroutineScope(Dispatchers.IO).launch {
//                delay(configFlow.value.scanDuration) // 5 seconds delay
//                adapter.bluetoothLeScanner.stopScan(scanCallback)
//                adapter.cancelDiscovery()
//            }
//        }
//    }
//
//    val scanCallback = object : ScanCallback() {
//        override fun onScanResult(callbackType: Int, result: ScanResult?) {
//            super.onScanResult(callbackType, result)
//            result?.let {
//                handleDeviceFound(it.device, System.currentTimeMillis(), it.rssi, true)
//            }
//        }
//    }
//
//    private val bluetoothManager =
//        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val name: String,
//        val alias: String,
//        val address: String,
//        val bondState: Int,
//        val connectionType: Int,
//        val classType: Int,
//        val rssi: Int,
//        val isLE: Boolean
//    ) : DataEntity(received)
//}