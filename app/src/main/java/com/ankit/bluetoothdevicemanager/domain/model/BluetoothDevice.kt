package com.ankit.bluetoothdevicemanager.domain.model

data class BluetoothDevice(
    val name: String?,
    val address: String,
    val rssi: Int,
    val bonded: Boolean
)
