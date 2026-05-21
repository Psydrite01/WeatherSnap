package com.example.weathersnap.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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

private const val ANIM_DURATION = 350

@Composable
fun NavigationShell(
    WeatherViewmodel: WeatherViewModel = hiltViewModel(),
    ReportsViewmodel: ReportsViewModel = hiltViewModel()
) {
    val NavController = rememberNavController()

    NavHost(
        navController  = NavController,
        startDestination = WeatherScreenRoute,
        // Fade used as the default for any route that doesn't override
        enterTransition = { fadeIn(tween(ANIM_DURATION)) },
        exitTransition  = { fadeOut(tween(ANIM_DURATION)) },
        popEnterTransition = { fadeIn(tween(ANIM_DURATION)) },
        popExitTransition  = { fadeOut(tween(ANIM_DURATION)) }
    ) {

        // ── WeatherScreen  (root / home) ──────────────────────────────────
        // Slides in from the left when popped back to; slides out to the left
        // when the user navigates forward.
        composable<WeatherScreenRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION)
                )
            }
        ) {
            WeatherScreen(
                navigateToCreateReport = { NavController.navigate(CreateReportScreenRoute) },
                viewModel              = WeatherViewmodel,
                navigateToSavedReports = { NavController.navigate(SavedReportsScreenRoute) }
            )
        }

        // ── CreateReportScreen  (slides in from right) ────────────────────
        composable<CreateReportScreenRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIM_DURATION)
                )
            }
        ) {
            CreateReportScreen(
                viewModel    = WeatherViewmodel,
                onOpenCamera = { NavController.navigate(CameraScreenRoute) },
                onBack       = { NavController.popBackStack() },
                onReportSaved = { NavController.navigate(SavedReportsScreenRoute) }
            )
        }

        // ── CameraScreen  (slides up from bottom, like a modal) ───────────
        composable<CameraScreenRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(ANIM_DURATION)
                )
            }
        ) {
            CameraScreen(
                onClose = { NavController.popBackStack() },
                onImageCaptured = { result ->
                    WeatherViewmodel.setCapturedImage(result)
                    NavController.popBackStack()
                }
            )
        }

        // ── SavedReportsScreen  (slides in from right) ────────────────────
        composable<SavedReportsScreenRoute>(
            enterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIM_DURATION)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards  = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(ANIM_DURATION)
                )
            }
        ) {
            SavedReportsScreen(
                onBack    = { NavController.popBackStack() },
                viewModel = ReportsViewmodel
            )
        }
    }
}