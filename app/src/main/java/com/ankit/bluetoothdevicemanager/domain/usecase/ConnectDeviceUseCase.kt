package com.ankit.bluetoothdevicemanager.domain.usecase

import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import javax.inject.Inject

class ConnectDeviceUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke(address: String) {
        repository.connect(address)
    }
}
