package com.ankit.bluetoothdevicemanager.presentation.scan

sealed class ScanEvent {
    data object StartScan : ScanEvent()
    data object StopScan : ScanEvent()
    data class ConnectDevice(val address: String) : ScanEvent()
    data class UpdateBluetoothStatus(val isEnabled: Boolean) : ScanEvent()
    data class UpdatePermissionStatus(val isGranted: Boolean) : ScanEvent()
    data class ShowPermissionDialog(val show: Boolean) : ScanEvent()
    data class ShowBluetoothDialog(val show: Boolean) : ScanEvent()
}
