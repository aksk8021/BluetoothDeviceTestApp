package com.ankit.bluetoothdevicemanager.domain.usecase

import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import javax.inject.Inject

class StartScanUseCase @Inject constructor(
    private val repository: BluetoothRepository
) {
    operator fun invoke() {
        repository.startScan()
    }
}
