package com.ankit.bluetoothdevicemanager.data.bluetooth

import com.ankit.bluetoothdevicemanager.domain.bluetooth.BluetoothManager
import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice
import com.ankit.bluetoothdevicemanager.domain.model.ConnectionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

class FakeBluetoothManager @Inject constructor() : BluetoothManager {

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices.asStateFlow()

    private val _scanningState = MutableStateFlow(false)
    override val scanningState: StateFlow<Boolean> = _scanningState.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow(true)
    override val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    override fun startScan() {
        // TODO: Implement fake scanning logic
    }

    override fun stopScan() {
        // TODO: Implement fake stop scan logic
    }

    override fun connect(address: String) {
        // TODO: Implement fake connect logic
    }

    override fun disconnect() {
        // TODO: Implement fake disconnect logic
    }
}
