package com.ankit.bluetoothdevicemanager.presentation.scan

import app.cash.turbine.test
import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import com.ankit.bluetoothdevicemanager.domain.usecase.ConnectDeviceUseCase
import com.ankit.bluetoothdevicemanager.domain.usecase.StartScanUseCase
import com.ankit.bluetoothdevicemanager.domain.usecase.StopScanUseCase
import com.ankit.bluetoothdevicemanager.domain.permission.PermissionManager
import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice
import com.ankit.bluetoothdevicemanager.domain.model.ConnectionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class ScanViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Mock
    private lateinit var repository: BluetoothRepository
    @Mock
    private lateinit var permissionManager: PermissionManager
    @Mock
    private lateinit var startScanUseCase: StartScanUseCase
    @Mock
    private lateinit var stopScanUseCase: StopScanUseCase
    @Mock
    private lateinit var connectDeviceUseCase: ConnectDeviceUseCase

    private lateinit var viewModel: ScanViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)

        `when`(repository.scannedDevices).thenReturn(MutableStateFlow(emptyList()))
        `when`(repository.scanningState).thenReturn(MutableStateFlow(false))
        `when`(repository.isBluetoothEnabled).thenReturn(MutableStateFlow(true))
        `when`(repository.connectionState).thenReturn(MutableStateFlow(ConnectionState.Disconnected))
        `when`(permissionManager.hasBluetoothPermissions()).thenReturn(true)
        `when`(permissionManager.getRequiredPermissions()).thenReturn(emptyList())

        viewModel = ScanViewModel(
            repository,
            permissionManager,
            startScanUseCase,
            stopScanUseCase,
            connectDeviceUseCase
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have permissionsGranted true when manager returns true`() = runTest {
        assertEquals(true, viewModel.state.value.permissionsGranted)
    }

    @Test
    fun `onEvent StartScan should call startScanUseCase`() = runTest {
        viewModel.onEvent(ScanEvent.StartScan)
        verify(startScanUseCase).invoke()
    }

    @Test
    fun `onEvent UpdatePermissionStatus should update state`() = runTest {
        viewModel.state.test {
            assertEquals(true, awaitItem().permissionsGranted)
            viewModel.onEvent(ScanEvent.UpdatePermissionStatus(false))
            assertEquals(false, awaitItem().permissionsGranted)
        }
    }
}
