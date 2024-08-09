package dev.iclab.tracker.collectors

import android.bluetooth.BluetoothDevice
import android.content.Context
import dev.iclab.tracker.BLEWrapper
import dev.iclab.tracker.database.DatabaseInterface

class TestBTCollector(
    override val context: Context,
    override val database: DatabaseInterface,
    override val bleWrapper: BLEWrapper
) : AbstractBTCollector(
    context, database, bleWrapper
) {
    override fun isTargetDevice(device: BluetoothDevice): Boolean {
        return device.name.contains("Galaxy Buds")
    }

    override fun start() {

    }

    override fun stop() {

    }

    override fun flush() {

    }
}