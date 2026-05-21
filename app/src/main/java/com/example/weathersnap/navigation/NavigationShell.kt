package com.example.weathersnap.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.weathersnap.ui.screens.CameraScreen
import com.example.weathersnap.ui.screens.CreateReportScreen
import com.example.weathersnap.ui.screens.SavedReportsScreen
import com.example.weathersnap.ui.screens.WeatherScreen
import com.example.weathersnap.ui.viewmodel.ReportsViewModel
import com.example.weathersnap.ui.viewmodel.WeatherViewModel

@Composable
fun NavigationShell(
    WeatherViewmodel: WeatherViewModel = hiltViewModel(),
    ReportsViewmodel: ReportsViewModel = hiltViewModel()
){
    val NavController = rememberNavController()

    NavHost(
        navController = NavController,
        startDestination = WeatherScreenRoute
    ){
        composable<WeatherScreenRoute> {
            WeatherScreen(
                navigateToCreateReport = {
                    NavController.navigate(CreateReportScreenRoute)
                },
                viewModel = WeatherViewmodel,
                navigateToSavedReports = {
                    NavController.navigate(SavedReportsScreenRoute)
                }
            )
        }
        composable<CreateReportScreenRoute> {
            CreateReportScreen(
                viewModel = WeatherViewmodel,
                onOpenCamera = {
                    NavController.navigate(CameraScreenRoute)
                },
                onBack = {
                    NavController.popBackStack()
                },
                onReportSaved = {
                    NavController.navigate(SavedReportsScreenRoute)
                }
            )
        }
        composable<CameraScreenRoute> {
            CameraScreen(
                onClose = {
                    NavController.popBackStack()
                },
                onImageCaptured = { result->
                    WeatherViewmodel.setCapturedImage(result)
                    NavController.popBackStack()
                }
            )
        }
        composable<SavedReportsScreenRoute> {
            SavedReportsScreen(
                onBack = {
                    NavController.popBackStack()
                },
                viewModel = ReportsViewmodel
            )
        }
    }
}