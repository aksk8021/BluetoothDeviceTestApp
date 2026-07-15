package com.ankit.bluetoothdevicemanager.presentation.scan

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import com.ankit.bluetoothdevicemanager.presentation.components.ScanContent
import com.ankit.bluetoothdevicemanager.ui.theme.BluetoothDeviceManagerTheme
import org.junit.Rule
import org.junit.Test

class ScanScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun scanContent_displaysTitleAndStatus() {
        // Given
        val state = ScanUiState(
            isBluetoothEnabled = true,
            permissionsGranted = true
        )

        // When
        composeTestRule.setContent {
            BluetoothDeviceManagerTheme {
                ScanContent(
                    state = state,
                    padding = PaddingValues(0.dp),
                    onEvent = {},
                    onPermissionRequest = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Bluetooth Status").assertIsDisplayed()
        composeTestRule.onNodeWithText("Start Scan").assertIsDisplayed()
    }

    @Test
    fun scanContent_showsBluetoothOffBanner_whenBluetoothIsDisabled() {
        // Given
        val state = ScanUiState(
            isBluetoothEnabled = false,
            permissionsGranted = true
        )

        // When
        composeTestRule.setContent {
            BluetoothDeviceManagerTheme {
                ScanContent(
                    state = state,
                    padding = PaddingValues(0.dp),
                    onEvent = {},
                    onPermissionRequest = {}
                )
            }
        }

        // Then
        composeTestRule.onNodeWithText("Bluetooth Off").assertIsDisplayed()
    }
}
