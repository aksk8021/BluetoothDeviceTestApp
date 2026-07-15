package com.ankit.bluetoothdevicemanager.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ankit.bluetoothdevicemanager.presentation.scan.ScanEvent
import com.ankit.bluetoothdevicemanager.presentation.scan.ScanUiState

@Composable
fun ScanContent(
    state: ScanUiState,
    padding: PaddingValues,
    onEvent: (ScanEvent) -> Unit,
    onPermissionRequest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        if (!state.permissionsGranted) {
            PermissionBanner(
                text = "Permission Required",
                modifier = Modifier.clickable { onPermissionRequest() }
            )
        } else if (!state.isBluetoothEnabled) {
            PermissionBanner(
                text = "Bluetooth Off",
                modifier = Modifier.clickable { onEvent(ScanEvent.ShowBluetoothDialog(true)) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            StatusCard(
                isBluetoothEnabled = state.isBluetoothEnabled,
                isScanning = state.isScanning,
                deviceCount = state.devices.size,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { onEvent(ScanEvent.StartScan) },
                    modifier = Modifier.weight(1f),
                    enabled = !state.isScanning && state.isBluetoothEnabled && state.permissionsGranted
                ) {
                    Text("Start Scan")
                }
                OutlinedButton(
                    onClick = { onEvent(ScanEvent.StopScan) },
                    modifier = Modifier.weight(1f),
                    enabled = state.isScanning
                ) {
                    Text("Stop Scan")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isScanning) {
                LoadingView()
            }

            if (!state.isScanning && state.devices.isEmpty()) {
                EmptyState()
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.devices, key = { it.address }) { device ->
                        DeviceCard(
                            device = device,
                            onClick = { onEvent(ScanEvent.ConnectDevice(device.address)) }
                        )
                    }
                }
            }
        }
    }
}
