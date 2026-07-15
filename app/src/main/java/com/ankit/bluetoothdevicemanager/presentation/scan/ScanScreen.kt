package com.ankit.bluetoothdevicemanager.presentation.scan

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ankit.bluetoothdevicemanager.presentation.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScanScreen(
    viewModel: ScanViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val activity = context as? Activity

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        viewModel.onEvent(ScanEvent.UpdatePermissionStatus(allGranted))
        if (!allGranted && activity != null) {
            val permanentlyDenied = permissions.filter { !it.value }.keys.any {
                !activity.shouldShowRequestPermissionRationale(it)
            }
            if (permanentlyDenied) {
                viewModel.onEvent(ScanEvent.ShowPermissionDialog(true))
            }
        }
    }

    val bluetoothLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        viewModel.onEvent(ScanEvent.UpdateBluetoothStatus(result.resultCode == Activity.RESULT_OK))
    }

    LaunchedEffect(Unit) {
        if (!state.permissionsGranted) {
            permissionLauncher.launch(viewModel.requiredPermissions.toTypedArray())
        }
    }

    if (state.showPermissionDialog) {
        PermissionRequiredDialog(
            onDismiss = { viewModel.onEvent(ScanEvent.ShowPermissionDialog(false)) },
            onOpenSettings = {
                viewModel.onEvent(ScanEvent.ShowPermissionDialog(false))
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
                context.startActivity(intent)
            }
        )
    }

    if (state.showBluetoothDialog) {
        EnableBluetoothDialog(
            onDismiss = { viewModel.onEvent(ScanEvent.ShowBluetoothDialog(false)) },
            onEnable = {
                viewModel.onEvent(ScanEvent.ShowBluetoothDialog(false))
                bluetoothLauncher.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Bluetooth Device Manager") }
            )
        }
    ) { padding ->
        ScanContent(
            state = state,
            padding = padding,
            onEvent = viewModel::onEvent,
            onPermissionRequest = { permissionLauncher.launch(viewModel.requiredPermissions.toTypedArray()) }
        )
    }
}
