package com.ankit.bluetoothdevicemanager.data.permission

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import com.ankit.bluetoothdevicemanager.domain.permission.PermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AndroidPermissionManager @Inject constructor(
    @ApplicationContext private val context: Context
) : PermissionManager {

    override fun hasBluetoothPermissions(): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(context, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
    }

    override fun getRequiredPermissions(): List<String> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            listOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            listOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }
    }

    /**
     * Note: This check usually requires an Activity reference to be 100% accurate 
     * using shouldShowRequestPermissionRationale. 
     */
    override fun isPermissionPermanentlyDenied(permission: String): Boolean {
        // This is a placeholder as the actual check happens in the UI layer 
        // where the Activity/Context is available for rationale check.
        return false 
    }
}
