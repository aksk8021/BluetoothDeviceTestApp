package com.ankit.bluetoothdevicemanager.domain.permission

interface PermissionManager {
    fun hasBluetoothPermissions(): Boolean
    fun getRequiredPermissions(): List<String>
    fun isPermissionPermanentlyDenied(permission: String): Boolean
}
