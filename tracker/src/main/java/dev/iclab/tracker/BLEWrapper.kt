package dev.iclab.tracker

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import java.util.UUID

class BLEWrapper(
    val context: Context,
    val registry: ActivityResultRegistry
) {
    enum class Status {
        DISABLED,
        READY,
        SCANNING,
        NOT_SUPPORTED
    }

    companion object {
        const val TAG = "BLEWrapper"
    }

    private var scannedDevices = mutableListOf<BluetoothDevice>()
//    val devices: Flow<BluetoothDevice>
    private var status = Status.DISABLED

    private var gatt: BluetoothGatt? = null


    private val bluetoothManager: BluetoothManager? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.getSystemService(BluetoothManager::class.java)
        } else {
            null
        }
    }

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            bluetoothManager?.adapter
        } else {
            BluetoothAdapter.getDefaultAdapter()
        }
    }

    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private val enableBTLauncher: ActivityResultLauncher<Intent> by lazy {
        registry.register("enableBT", ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                status = Status.READY
            } else {
                Log.d(TAG, "Bluetooth is not enabled")
            }
        }
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d("BLE", "Connected to GATT server.")
                // 서비스 검색을 시작합니다.
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d("BLE", "Disconnected from GATT server.")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Services discovered.")
                // 필요한 서비스와 특성을 찾고 데이터 통신을 수행합니다.
            } else {
                Log.w("BLE", "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicRead(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Characteristic read: ${characteristic.value}")
            }
        }

        override fun onCharacteristicWrite(gatt: BluetoothGatt, characteristic: BluetoothGattCharacteristic, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                Log.d("BLE", "Characteristic written: ${characteristic.value}")
            }
        }
    }

    fun getConnectedDevices(): List<BluetoothDevice> {
        return bluetoothManager?.getConnectedDevices(BluetoothProfile.GATT) ?: listOf()
    }

    fun getStatus(): Status {
        return status
    }

    fun enable() {
        if (bluetoothAdapter == null) {
            Log.e(TAG, "BluetoothAdapter is null")
            return
        }
        if (!bluetoothAdapter!!.isEnabled) {
            val enableBTIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            enableBTLauncher.launch(enableBTIntent)
        }
    }

    val scanLeCallback: ScanCallback = object: ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            result?.device?.let {
                scannedDevices.add(it)
            }
        }
    }

    fun scan() {
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "BluetoothLeScanner is null")
            return
        }else {
            bluetoothLeScanner!!.startScan(scanLeCallback)
        }
    }

    fun stopScan() {
        if (bluetoothLeScanner == null) {
            Log.e(TAG, "BluetoothLeScanner is null")
            return
        } else {
            bluetoothLeScanner!!.stopScan(scanLeCallback)
        }
    }



    fun connect(device: BluetoothDevice) {
        gatt = device.connectGatt(context, false, gattCallback)
    }

    fun disconnect(){
        gatt?.disconnect()
        gatt?.close()
        gatt = null
    }

}