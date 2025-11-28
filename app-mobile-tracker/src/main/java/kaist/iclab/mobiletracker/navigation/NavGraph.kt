package kaist.iclab.mobiletracker.navigation

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kaist.iclab.mobiletracker.ui.screens.DataScreen.DataScreen
import kaist.iclab.mobiletracker.ui.screens.HomeScreen.HomeScreen
import kaist.iclab.mobiletracker.ui.screens.LoginScreen.LoginScreen
import kaist.iclab.mobiletracker.ui.screens.MessageScreen.MessageScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.AboutSettings.AboutSettingsScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.AccountSettings.AccountSettingsScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DevicesSettings.DevicesSettingsScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.LanguageSettings.LanguageScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PermissionSettings.PermissionSettingsScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensorSettings.PhoneSensorScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.ServerSyncSettings.ServerSyncSettingsScreen
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.SettingsScreen
import kaist.iclab.mobiletracker.viewmodels.auth.AuthViewModel
import kaist.iclab.tracker.permission.AndroidPermissionManager

/**
 * Navigation graph for the app.
 */
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String = Screen.Login.route,
    permissionManager: AndroidPermissionManager
) {
    val userState by authViewModel.userState.collectAsState()
    val context = LocalContext.current
    val activity = context as? Activity

    // Handle language change by recreating activity
    val onLanguageChanged: () -> Unit = {
        if (activity != null) {
            activity.recreate()
        }
    }

    // Navigate based on authentication state
    LaunchedEffect(userState.isLoggedIn) {
        val mainTabs = listOf(Screen.Home.route, Screen.Data.route, Screen.Message.route, Screen.Setting.route)
        val currentRoute = navController.currentDestination?.route
        
        if (userState.isLoggedIn) {
            // Navigate to Home screen (main tab) when user logs in
            if (currentRoute !in mainTabs) {
                navController.navigate(Screen.Home.route) {
                    // Clear back stack to prevent going back to login
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
        } else {
            // Navigate to Login when user logs out
            if (currentRoute !in mainTabs && currentRoute != Screen.Login.route) {
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
                },
                onLanguageChanged = onLanguageChanged
            )
        }

        // Main tabs - each has BackHandler to close app when back is pressed
        composable(route = Screen.Home.route) {
            BackHandler {
                activity?.finish()
            }
            HomeScreen(authViewModel = authViewModel)
        }

        composable(route = Screen.Data.route) {
            BackHandler {
                activity?.finish()
            }
            DataScreen()
        }

        composable(route = Screen.Message.route) {
            BackHandler {
                activity?.finish()
            }
            MessageScreen()
        }

        composable(route = Screen.Setting.route) {
            BackHandler {
                activity?.finish()
            }
            SettingsScreen(navController = navController)
        }

        // Settings sub-screens
        composable(route = Screen.PhoneSensor.route) {
            PhoneSensorScreen(navController = navController)
        }

        composable(route = Screen.Language.route) {
            LanguageScreen(
                navController = navController,
                onLanguageChanged = onLanguageChanged
            )
        }

        composable(route = Screen.Account.route) {
            AccountSettingsScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(route = Screen.Permission.route) {
            PermissionSettingsScreen(
                navController = navController,
                permissionManager = permissionManager
            )
        }

        composable(route = Screen.ServerSync.route) {
            ServerSyncSettingsScreen(navController = navController)
        }

        composable(route = Screen.Devices.route) {
            DevicesSettingsScreen(navController = navController)
        }

        composable(route = Screen.About.route) {
            AboutSettingsScreen(navController = navController)
        }
    }
}

