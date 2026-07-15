package com.ankit.bluetoothdevicemanager.domain.repository

import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice
import com.ankit.bluetoothdevicemanager.domain.model.ConnectionState
import kotlinx.coroutines.flow.StateFlow

interface BluetoothRepository {
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val scanningState: StateFlow<Boolean>
    val isBluetoothEnabled: StateFlow<Boolean>
    val connectionState: StateFlow<ConnectionState>
    
    fun startScan()
    fun stopScan()
    fun connect(address: String)
    fun disconnect()
}
