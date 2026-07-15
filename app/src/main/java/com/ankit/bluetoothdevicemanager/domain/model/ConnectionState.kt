package com.ankit.bluetoothdevicemanager.domain.model

sealed interface ConnectionState {

    data object Disconnected : ConnectionState

    data object Scanning : ConnectionState

    data object Connecting : ConnectionState

    data object Connected : ConnectionState

    data class Error(
        val message: String
    ) : ConnectionState
}