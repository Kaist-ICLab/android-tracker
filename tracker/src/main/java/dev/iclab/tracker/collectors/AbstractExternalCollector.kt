package dev.iclab.tracker.collectors

import android.bluetooth.BluetoothDevice
import android.content.Context
import dev.iclab.tracker.database.DatabaseInterface

abstract class AbstractBTCollector(
    override val context: Context,
    override val database: DatabaseInterface
):AbstractCollector(
    context, database
) {

    override val permissions: Array<String> = arrayOf()

    abstract override fun isAvailable():Boolean

    abstract override suspend fun enable():Boolean

    abstract override fun start()

    abstract override fun stop()

    abstract override fun flush()

    abstract fun isCorrectDevice(device: BluetoothDevice): Boolean

    abstract fun startConnection(device: BluetoothDevice)

    abstract fun endConnection(device: BluetoothDevice)
}