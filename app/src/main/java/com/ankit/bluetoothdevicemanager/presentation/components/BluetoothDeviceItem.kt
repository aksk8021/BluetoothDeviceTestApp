package com.ankit.bluetoothdevicemanager.presentation.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice

@Composable
fun BluetoothDeviceItem(
    device: BluetoothDevice,
    onClick: () -> Unit
) {
    // TODO: Implement device list item
    Text(text = device.name ?: "Unknown Device")
}
