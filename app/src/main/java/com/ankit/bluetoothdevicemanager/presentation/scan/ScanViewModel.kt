package com.ankit.bluetoothdevicemanager.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ankit.bluetoothdevicemanager.domain.model.ConnectionState
import com.ankit.bluetoothdevicemanager.domain.permission.PermissionManager
import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import com.ankit.bluetoothdevicemanager.domain.usecase.ConnectDeviceUseCase
import com.ankit.bluetoothdevicemanager.domain.usecase.StartScanUseCase
import com.ankit.bluetoothdevicemanager.domain.usecase.StopScanUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject constructor(
    private val repository: BluetoothRepository,
    private val permissionManager: PermissionManager,
    private val startScanUseCase: StartScanUseCase,
    private val stopScanUseCase: StopScanUseCase,
    private val connectDeviceUseCase: ConnectDeviceUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ScanUiState(
        permissionsGranted = permissionManager.hasBluetoothPermissions()
    ))
    val state: StateFlow<ScanUiState> = _state.asStateFlow()

    val requiredPermissions: List<String> = permissionManager.getRequiredPermissions()

    init {
        repository.scannedDevices.onEach { devices ->
            _state.value = _state.value.copy(devices = devices)
        }.launchIn(viewModelScope)

        repository.scanningState.onEach { isScanning ->
            _state.value = _state.value.copy(isScanning = isScanning)
        }.launchIn(viewModelScope)

        repository.isBluetoothEnabled.onEach { isEnabled ->
            _state.value = _state.value.copy(isBluetoothEnabled = isEnabled)
        }.launchIn(viewModelScope)

        repository.connectionState.onEach { connectionState ->
            when (connectionState) {
                is ConnectionState.Error -> {
                    _state.value = _state.value.copy(errorMessage = connectionState.message)
                }
                else -> {
                    _state.value = _state.value.copy(errorMessage = null)
                }
            }
        }.launchIn(viewModelScope)
    }

    fun onEvent(event: ScanEvent) {
        when (event) {
            ScanEvent.StartScan -> startScanUseCase()
            ScanEvent.StopScan -> stopScanUseCase()
            is ScanEvent.ConnectDevice -> connectDeviceUseCase(event.address)
            is ScanEvent.UpdateBluetoothStatus -> {
                _state.value = _state.value.copy(isBluetoothEnabled = event.isEnabled)
            }
            is ScanEvent.UpdatePermissionStatus -> {
                _state.value = _state.value.copy(permissionsGranted = event.isGranted)
            }
            is ScanEvent.ShowPermissionDialog -> {
                _state.value = _state.value.copy(showPermissionDialog = event.show)
            }
            is ScanEvent.ShowBluetoothDialog -> {
                _state.value = _state.value.copy(showBluetoothDialog = event.show)
            }
        }
    }
}
