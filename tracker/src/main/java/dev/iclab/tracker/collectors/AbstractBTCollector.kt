package dev.iclab.tracker.collectors

import android.bluetooth.BluetoothDevice
import android.content.Context
import dev.iclab.tracker.BLEWrapper
import dev.iclab.tracker.database.DatabaseInterface
import dev.iclab.tracker.filters.Filter

abstract class AbstractBTCollector(
    override val context: Context,
    override val database: DatabaseInterface,
    open val bleWrapper: BLEWrapper
) : AbstractCollector(
    context, database
) {
    companion object{
        const val TAG = "AbstractBTCollector"
    }

    override val permissions: Array<String> = arrayOf()
    override val filters: MutableList<Filter> = mutableListOf()

    private var connected: BluetoothDevice? = null

    /* Check whether bluetooth is available or not */
    override fun isAvailable(): Boolean {
        return bleWrapper.getStatus() != BLEWrapper.Status.NOT_SUPPORTED
    }

    abstract fun isTargetDevice(device: BluetoothDevice): Boolean

    override suspend fun enable() {
        // 블루투스 비활 시 활성화 요청
        if (bleWrapper.getStatus() == BLEWrapper.Status.DISABLED) {
            bleWrapper.enable()
        }
        else if (bleWrapper.getStatus() == BLEWrapper.Status.READY) {
            val connectedTargetDevice = bleWrapper.getConnectedDevices().filter{
                isTargetDevice(it)
            }
            if (connectedTargetDevice.isEmpty()) {
                bleWrapper.scan()
//              리스트를 보여주어야 함.
            } else if (connectedTargetDevice.size == 1) {
                connected = connectedTargetDevice[0]
//              연결된 디바이스를 보여주어야 함.
            } else {
                throw Exception("Multiple target devices are connected")
            }
        }
    }


    abstract override fun start()

    abstract override fun stop()

    abstract override fun flush()
}