package com.ankit.bluetoothdevicemanager.data.mapper

import android.annotation.SuppressLint
import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice

/**
 * Maps android.bluetooth.BluetoothDevice to the domain model BluetoothDevice.
 * Includes RSSI which is obtained from the scan result.
 */
@SuppressLint("MissingPermission")
fun android.bluetooth.BluetoothDevice.toBluetoothDevice(rssi: Int): BluetoothDevice {
    return BluetoothDevice(
        name = name,
        address = address,
        rssi = rssi,
        bonded = bondState == android.bluetooth.BluetoothDevice.BOND_BONDED
    )
}
