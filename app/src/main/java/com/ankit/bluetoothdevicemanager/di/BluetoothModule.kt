package com.ankit.bluetoothdevicemanager.di

import com.ankit.bluetoothdevicemanager.data.bluetooth.AndroidBluetoothManager
import com.ankit.bluetoothdevicemanager.data.permission.AndroidPermissionManager
import com.ankit.bluetoothdevicemanager.data.repository.BluetoothRepositoryImpl
import com.ankit.bluetoothdevicemanager.domain.bluetooth.BluetoothManager
import com.ankit.bluetoothdevicemanager.domain.permission.PermissionManager
import com.ankit.bluetoothdevicemanager.domain.repository.BluetoothRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothModule {

    @Binds
    @Singleton
    abstract fun bindBluetoothManager(
        androidBluetoothManager: AndroidBluetoothManager
    ): BluetoothManager

    @Binds
    @Singleton
    abstract fun bindPermissionManager(
        androidPermissionManager: AndroidPermissionManager
    ): PermissionManager

    @Binds
    @Singleton
    abstract fun bindBluetoothRepository(
        bluetoothRepositoryImpl: BluetoothRepositoryImpl
    ): BluetoothRepository
}
