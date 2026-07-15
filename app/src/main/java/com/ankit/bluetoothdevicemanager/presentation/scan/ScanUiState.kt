package com.ankit.bluetoothdevicemanager.presentation.scan

import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice

data class ScanUiState(
    val devices: List<BluetoothDevice> = emptyList(),
    val isScanning: Boolean = false,
    val isBluetoothEnabled: Boolean = false,
    val permissionsGranted: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val showBluetoothDialog: Boolean = false,
    val errorMessage: String? = null
)
