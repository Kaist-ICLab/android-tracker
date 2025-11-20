package kaist.iclab.mobiletracker.ui.screens.MainScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import kaist.iclab.mobiletracker.navigation.NavGraph
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.ui.BottomNavigationBar
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.AuthViewModel

/**
 * Main screen with bottom navigation bar.
 * Contains the main content area and bottom navigation.
 */
@Composable
fun MainScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            // Show bottom navigation only for main tabs (not for Login)
            if (currentRoute in listOf(Screen.Home.route, Screen.Data.route, Screen.Message.route, Screen.Setting.route)) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            NavGraph(
                navController = navController,
                authViewModel = authViewModel,
                startDestination = startDestination
            )
        }
    }
}

