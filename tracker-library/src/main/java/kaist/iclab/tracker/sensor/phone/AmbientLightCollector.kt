package kaist.iclab.tracker.sensor.phone

//class AmbientLightCollector(
//    val context: Context,
//    permissionManager: PermissionManagerInterface,
//) : AbstractCollector<AmbientLightCollector.Config, AmbientLightCollector.Entity>(permissionManager) {
//    override val permissions = listOfNotNull<String>().toTypedArray()
//    override val foregroundServiceTypes: Array<Int> = listOfNotNull<Int>().toTypedArray()
//
//    data class Config(
//        val interval: Long
//    ) : CollectorConfig()
//
//    override val _defaultConfig = Config(
//        TimeUnit.MINUTES.toMillis(3)
//    )
//
//    // Check whether there is a light sensor
//    override fun isAvailable(): Availability {
//        val status = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) != null
//        return Availability(status, if (status) null else "AmbientLight Sensor is not available")
//    }
//
//    override fun start() {
//        super.start()
//        sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)?.let { sensor ->
//            sensorManager.registerListener(
//                sensorEventListener,
//                sensor,
//                TimeUnit.MILLISECONDS.toMicros(configFlow.value.interval).toInt()
//            )
//        }
//    }
//
//    override fun stop() {
//        sensorManager.unregisterListener(sensorEventListener)
//        super.stop()
//    }
//
//    data class Entity(
//        override val received: Long,
//        val timestamp: Long,
//        val accuracy: Int,
//        val value: Float
//    ) : DataEntity(received)
//
//    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
//
//    private val sensorEventListener = object:SensorEventListener {
//        override fun onAccuracyChanged(sensor: Sensor?, p1: Int) {}
//        override fun onSensorChanged(event: SensorEvent?) {
//            val timestamp = System.currentTimeMillis()
//            event?.let {
//                listener?.invoke(
//                    Entity(
//                        timestamp,
//                        it.timestamp,
//                        it.accuracy,
//                        it.values[0]
//                    )
//                )
//            }
//        }
//    }
//
//
//}