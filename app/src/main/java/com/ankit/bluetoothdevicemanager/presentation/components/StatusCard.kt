package com.ankit.bluetoothdevicemanager.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusCard(
    isBluetoothEnabled: Boolean,
    isScanning: Boolean,
    deviceCount: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatusRow(
                label = "Bluetooth Status",
                value = if (isBluetoothEnabled) "On" else "Off",
                valueColor = if (isBluetoothEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
            )
            StatusRow(
                label = "Scanning Status",
                value = if (isScanning) "Scanning..." else "Idle",
                valueColor = if (isScanning) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            StatusRow(
                label = "Discovered Devices",
                value = deviceCount.toString(),
                valueColor = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun StatusRow(
    label: String,
    value: String,
    valueColor: Color
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
            color = valueColor
        )
    }
}
