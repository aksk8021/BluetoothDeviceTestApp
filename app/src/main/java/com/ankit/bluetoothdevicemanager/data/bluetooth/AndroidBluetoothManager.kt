package com.ankit.bluetoothdevicemanager.data.bluetooth

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.location.LocationManager
import com.ankit.bluetoothdevicemanager.data.mapper.toBluetoothDevice
import com.ankit.bluetoothdevicemanager.domain.bluetooth.BluetoothManager as DomainBluetoothManager
import com.ankit.bluetoothdevicemanager.domain.model.BluetoothDevice
import com.ankit.bluetoothdevicemanager.domain.model.ConnectionState
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Real implementation of BluetoothManager using Android Bluetooth APIs.
 * Supports BLE scanning for API 26+.
 */
@SuppressLint("MissingPermission")
class AndroidBluetoothManager @Inject constructor(
    @ApplicationContext private val context: Context
) : DomainBluetoothManager {

    private val bluetoothManager by lazy {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        bluetoothManager.adapter
    }

    private val _scannedDevices = MutableStateFlow<List<BluetoothDevice>>(emptyList())
    override val scannedDevices: StateFlow<List<BluetoothDevice>> = _scannedDevices.asStateFlow()

    private val _scanningState = MutableStateFlow(false)
    override val scanningState: StateFlow<Boolean> = _scanningState.asStateFlow()

    private val _isBluetoothEnabled = MutableStateFlow(bluetoothAdapter?.isEnabled == true)
    override val isBluetoothEnabled: StateFlow<Boolean> = _isBluetoothEnabled.asStateFlow()

    private val _connectionState = MutableStateFlow<ConnectionState>(ConnectionState.Disconnected)
    override val connectionState: StateFlow<ConnectionState> = _connectionState.asStateFlow()

    private var scanJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    /**
     * Receiver to handle Bluetooth state changes and Classic discovery results.
     */
    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            stopScan()
                            _isBluetoothEnabled.update { false }
                            _connectionState.update { ConnectionState.Error("Bluetooth was turned off") }
                        }
                        BluetoothAdapter.STATE_ON -> {
                            _isBluetoothEnabled.update { true }
                            _connectionState.update { ConnectionState.Disconnected }
                        }
                    }
                }
                android.bluetooth.BluetoothDevice.ACTION_FOUND -> {
                    val device = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE, android.bluetooth.BluetoothDevice::class.java)
                    } else {
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra(android.bluetooth.BluetoothDevice.EXTRA_DEVICE)
                    }
                    val rssi = intent.getShortExtra(android.bluetooth.BluetoothDevice.EXTRA_RSSI, -100).toInt()
                    
                    device?.let { 
                        updateDeviceList(it.toBluetoothDevice(rssi))
                    }
                }
            }
        }
    }

    /**
     * Callback for BLE scan results.
     */
    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            // Use the mapper to convert android device to domain model
            val device = result.device.toBluetoothDevice(result.rssi)
            updateDeviceList(device)
        }

        override fun onBatchScanResults(results: MutableList<ScanResult>) {
            super.onBatchScanResults(results)
            results.forEach { result ->
                val device = result.device.toBluetoothDevice(result.rssi)
                updateDeviceList(device)
            }
        }

        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            _scanningState.update { false }
            val errorMessage = when (errorCode) {
                SCAN_FAILED_ALREADY_STARTED -> "Scan already started"
                SCAN_FAILED_APPLICATION_REGISTRATION_FAILED -> "Application registration failed"
                SCAN_FAILED_INTERNAL_ERROR -> "Internal error"
                SCAN_FAILED_FEATURE_UNSUPPORTED -> "Feature unsupported"
                else -> "Scan failed with error code: $errorCode"
            }
            _connectionState.update { ConnectionState.Error(errorMessage) }
        }
    }

    init {
        // Register receiver for Bluetooth state changes and Classic device discovery
        val filter = IntentFilter().apply {
            addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
            addAction(android.bluetooth.BluetoothDevice.ACTION_FOUND)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
            addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        }
        context.registerReceiver(bluetoothStateReceiver, filter)
    }

    /**
     * Updates the scanned devices list, avoiding duplicates and sorting by RSSI.
     */
    private fun updateDeviceList(device: BluetoothDevice) {
        _scannedDevices.update { devices ->
            val existingIndex = devices.indexOfFirst { it.address == device.address }
            val newDevices = if (existingIndex != -1) {
                devices.toMutableList().apply {
                    this[existingIndex] = device
                }
            } else {
                devices + device
            }
            // Sort by RSSI descending as per requirement
            newDevices.sortedByDescending { it.rssi }
        }
    }

    override fun startScan() {
        if (_scanningState.value) return

        if (!hasPermissions()) {
            _connectionState.update { ConnectionState.Error("Missing Bluetooth permissions") }
            return
        }

        if (!isLocationEnabled()) {
            _connectionState.update { ConnectionState.Error("Location Services must be enabled to discover devices") }
            return
        }

        if (bluetoothAdapter == null) {
            _connectionState.update { ConnectionState.Error("Bluetooth not supported on this device") }
            return
        }

        if (!bluetoothAdapter!!.isEnabled) {
            _connectionState.update { ConnectionState.Error("Bluetooth is disabled") }
            return
        }

        _scannedDevices.update { emptyList() }
        _scanningState.update { true }
        _connectionState.update { ConnectionState.Scanning }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        try {
            bluetoothAdapter?.bluetoothLeScanner?.startScan(null, settings, scanCallback)
            bluetoothAdapter?.startDiscovery()

            // Auto stop scan after 30 seconds as per requirement
            scanJob?.cancel()
            scanJob = scope.launch {
                delay(30_000L)
                stopScan()
            }
        } catch (e: Exception) {
            _scanningState.update { false }
            _connectionState.update { ConnectionState.Error("Failed to start scan: ${e.message}") }
        }
    }

    override fun stopScan() {
        if (!_scanningState.value) return

        try {
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(scanCallback)
            bluetoothAdapter?.cancelDiscovery()
        } catch (e: Exception) {
            // Ignore if scanner is null or already stopped
        }

        _scanningState.update { false }
        _connectionState.update { ConnectionState.Disconnected }
        scanJob?.cancel()
        scanJob = null
    }

    override fun connect(address: String) {
        // To be implemented in next phase
    }

    override fun disconnect() {
        // To be implemented in next phase
    }

    private fun hasPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_SCAN) == android.content.pm.PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(android.Manifest.permission.BLUETOOTH_CONNECT) == android.content.pm.PackageManager.PERMISSION_GRANTED
        } else {
            context.checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) == android.content.pm.PackageManager.PERMISSION_GRANTED
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    /**
     * Clean up resources when the manager is no longer needed.
     * Note: In a real app, this might be called from a ViewModel's onCleared or similar.
     */
    fun release() {
        try {
            context.unregisterReceiver(bluetoothStateReceiver)
        } catch (e: Exception) {
            // Already unregistered
        }
        stopScan()
    }
}
