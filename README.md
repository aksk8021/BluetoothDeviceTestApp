# Bluetooth Device Manager

A modern Android application for discovering and managing Bluetooth devices, built with **Jetpack Compose**, **Clean Architecture**, and **Material 3**.

## 🚀 Features

- **Real-time Bluetooth Scanning**: Discover nearby Bluetooth (Classic & BLE) devices.
- **Permission Handling**: Production-ready runtime permission management for Android 8.0 to Android 15+.
- **Device Details**: View device name, MAC address, signal strength (RSSI), and bond status.
- **Smart UI**: Context-aware buttons, interactive banners for Bluetooth/Location status, and smooth animations.
- **Dark Mode Support**: Full support for system-wide dark and light themes.

## 🛠️ Tech Stack

- **UI**: Jetpack Compose, Material 3, Navigation Compose.
- **Architecture**: MVVM + Clean Architecture (Data, Domain, Presentation layers).
- **Dependency Injection**: Hilt.
- **Async/Reactive**: Kotlin Coroutines & Flow (StateFlow).
- **Testing**: JUnit 4, Mockito, Turbine, Compose UI Testing.

## 🏗️ Project Structure

- `data`: Implementation of repositories and Bluetooth managers using Android Framework APIs.
- `domain`: Business logic, interfaces, and models (pure Kotlin).
- `presentation`: UI components, ViewModels, and state management.
- `di`: Hilt modules for dependency injection.

## 🚦 Getting Started

1. Clone the repository.
2. Open in Android Studio (Jellyfish or newer).
3. Ensure you have a device with Bluetooth support.
4. Run the `:app` module.

## 🧪 Testing

The project includes unit tests for the domain/viewmodel layers and UI tests for Compose components.
- **Unit Tests**: Run `./gradlew test`
- **UI Tests**: Run `./gradlew connectedAndroidTest`

## 🛡️ Permissions

The app handles the following permissions gracefully:
- `BLUETOOTH_SCAN` & `BLUETOOTH_CONNECT` (Android 12+)
- `ACCESS_FINE_LOCATION` (Android 11 and below)
