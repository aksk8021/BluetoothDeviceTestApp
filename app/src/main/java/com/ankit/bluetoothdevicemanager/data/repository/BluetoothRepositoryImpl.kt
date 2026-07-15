package com.ankit.bluetoothdevicemanager.data.repository

import com.ankit.bluetoothdevicemanager.domain.bluetooth.BluetoothManager
import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice
import com.ankit.bluetoothdevicemanager.domain.model.ConnectionState
import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class BluetoothRepositoryImpl @Inject constructor(
    private val bluetoothManager: BluetoothManager
) : BluetoothRepository {

    override val scannedDevices: StateFlow<List<BluetoothDevice>> = bluetoothManager.scannedDevices
    override val scanningState: StateFlow<Boolean> = bluetoothManager.scanningState
    override val isBluetoothEnabled: StateFlow<Boolean> = bluetoothManager.isBluetoothEnabled
    override val connectionState: StateFlow<ConnectionState> = bluetoothManager.connectionState

    override fun startScan() {
        bluetoothManager.startScan()
    }

    override fun stopScan() {
        bluetoothManager.stopScan()
    }

    override fun connect(address: String) {
        bluetoothManager.connect(address)
    }

    override fun disconnect() {
        bluetoothManager.disconnect()
    }
}
