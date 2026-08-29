package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.sensor.ShakeDetector
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LegalScreen
import com.example.ui.theme.DeepSpaceNavy
import com.example.ui.theme.MyApplicationTheme

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Legal : Screen("legal")
}

class MainActivity : ComponentActivity() {

    private val viewModel: DiceViewModel by viewModels()
    private var shakeDetector: ShakeDetector? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Setup hardware sensor shake listener (2.7G Threshold with Low-Pass filter)
        shakeDetector = ShakeDetector(
            context = this,
            thresholdG = ShakeDetector.DEFAULT_SHAKE_THRESHOLD_G,
            slopTimeMs = ShakeDetector.DEFAULT_SLOP_TIME_MS
        ) {
            viewModel.onShakeDetected()
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = DeepSpaceNavy
                ) {
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector?.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector?.stop()
    }
}

@Composable
fun AppNavigation(
    viewModel: DiceViewModel,
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeIn(animationSpec = tween(350))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(350)
            ) + fadeOut(animationSpec = tween(350))
        }
    ) {
        composable(route = Screen.Home.route) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToLegal = {
                    navController.navigate(Screen.Legal.route)
                }
            )
        }

        composable(route = Screen.Legal.route) {
            LegalScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onSubtleHaptic = {
                    viewModel.performSubtleHaptic()
                }
            )
        }
    }
}
