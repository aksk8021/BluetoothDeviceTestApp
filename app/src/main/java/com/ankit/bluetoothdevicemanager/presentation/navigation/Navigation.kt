package com.ankit.bluetoothdevicemanager.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ankit.bluetoothdevicemanager.presentation.details.DetailsScreen
import com.ankit.bluetoothdevicemanager.presentation.scan.ScanScreen
import com.ankit.bluetoothdevicemanager.presentation.settings.SettingsScreen

sealed class Screen(val route: String) {
    object Scan : Screen("scan")
    object Details : Screen("details/{address}") {
        fun createRoute(address: String) = "details/$address"
    }
    object Settings : Screen("settings")
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Scan.route
    ) {
        composable(Screen.Scan.route) {
            ScanScreen()
        }
        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument("address") { nullable = true })
        ) { backStackEntry ->
            val address = backStackEntry.arguments?.getString("address")
            DetailsScreen(deviceAddress = address)
        }
        composable(Screen.Settings.route) {
            SettingsScreen()
        }
    }
}
