package com.ankit.bluetoothdevicemanager.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.ankit.bluetoothdevicemanager.presentation.navigation.Navigation
import com.ankit.bluetoothdevicemanager.ui.theme.BluetoothDeviceManagerTheme

@Composable
fun App() {
    BluetoothDeviceManagerTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            Navigation(navController = navController)
        }
    }
}
