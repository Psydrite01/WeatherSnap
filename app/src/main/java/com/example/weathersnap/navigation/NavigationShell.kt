package com.example.weathersnap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersnap.ui.screens.CameraScreen
import com.example.weathersnap.ui.screens.CreateReportScreen
import com.example.weathersnap.ui.screens.SavedReportsScreen
import com.example.weathersnap.ui.screens.WeatherScreen

@Composable
fun NavigationShell(){
    val NavController = rememberNavController()

    NavHost(
        navController = NavController,
        startDestination = WeatherScreenRoute
    ){
        composable<WeatherScreenRoute> {
            WeatherScreen()
        }
        composable<CreateReportScreenRoute> {
            CreateReportScreen()
        }
        composable<CameraScreenRoute> {
            CameraScreen()
        }
        composable<SavedReportsScreenRoute> {
            SavedReportsScreen()
        }
    }
}