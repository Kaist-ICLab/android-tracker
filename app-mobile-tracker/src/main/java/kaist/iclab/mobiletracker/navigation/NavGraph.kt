package kaist.iclab.mobiletracker.navigation

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kaist.iclab.mobiletracker.ui.DataScreen
import kaist.iclab.mobiletracker.ui.HomeScreen
import kaist.iclab.mobiletracker.ui.LoginScreen
import kaist.iclab.mobiletracker.ui.MessageScreen
import kaist.iclab.mobiletracker.ui.SettingsScreen
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel

/**
 * Navigation graph for the app.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String = Screen.Login.route
) {
    val userState by authViewModel.userState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Navigate based on authentication state
    LaunchedEffect(userState.isLoggedIn) {
        if (userState.isLoggedIn) {
            // Navigate to Home screen (main tab) when user logs in
            val mainTabs = listOf(Screen.Home.route, Screen.Data.route, Screen.Message.route, Screen.Setting.route)
            val currentRoute = navController.currentDestination?.route
            if (currentRoute !in mainTabs) {
                navController.navigate(Screen.Home.route) {
                    // Clear back stack to prevent going back to login
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        } else {
            // Navigate to Login when user logs out
            if (navController.currentDestination?.route != Screen.Login.route) {
                navController.navigate(Screen.Login.route) {
                    // Clear back stack to prevent going back to main tabs
                    popUpTo(0) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.Login.route) {
            LoginScreen(
                onSignInWithGoogle = {
                    if (activity != null) {
                        authViewModel.login(activity)
                    }
                }
            )
        }

        // Main tabs
        composable(route = Screen.Home.route) {
            HomeScreen(viewModel = authViewModel)
        }

        composable(route = Screen.Data.route) {
            DataScreen()
        }

        composable(route = Screen.Message.route) {
            MessageScreen()
        }

        composable(route = Screen.Setting.route) {
            SettingsScreen()
        }
    }
}

