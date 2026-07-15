package com.ankit.bluetoothdevicemanager.domain.usecase

import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class StartScanUseCaseTest {

    @Mock
    private lateinit var repository: BluetoothRepository

    private lateinit var startScanUseCase: StartScanUseCase

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        startScanUseCase = StartScanUseCase(repository)
    }

    @Test
    fun `invoke should call startScan on repository`() {
        // When
        startScanUseCase()

        // Then
        verify(repository).startScan()
    }
}
