package kaist.iclab.field_tracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.screens.DataConfigScreen
import kaist.iclab.field_tracker.ui.screens.LoginScreen
import kaist.iclab.field_tracker.ui.screens.PermissionListScreen
import kaist.iclab.field_tracker.ui.screens.SettingScreen
import kaist.iclab.field_tracker.ui.screens.UserProfileScreen

enum class AppScreens(name: String) {
    Login("login"),
    Setting("setting"),
    PermissionList("permission_list"),
    UserProfile("user_profile"),
    DataConfig("data_config"),
}

@Composable
fun MainApp(
    navController: NavHostController = rememberNavController()
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: AppScreens.Login.name

    Scaffold(
        topBar = {
            if(currentScreen!= AppScreens.Login.name) {
                Header(
                    title = "The Tracker",
                    canNavigateBack = navController.previousBackStackEntry != null,
                    navigateBack = { navController.popBackStack() }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = currentScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable(route = AppScreens.Login.name) {
                LoginScreen(
                    onLogin = {},
                    onTestWithoutLogin = { navController.navigate(AppScreens.Setting.name) }
                )
            }
            composable(route = AppScreens.Setting.name) {
                SettingScreen(
                    onNavigateToPermissionList = { navController.navigate(AppScreens.PermissionList.name) },
                    onNavigateToUserProfile = { navController.navigate(AppScreens.UserProfile.name) },
                    onNavigateToDataConfig = { navController.navigate(AppScreens.DataConfig.name) },
                )
            }
            composable(route = AppScreens.PermissionList.name) {
                PermissionListScreen()
            }
            composable(route = AppScreens.UserProfile.name) {
                UserProfileScreen()
            }
            composable(route = AppScreens.DataConfig.name) {
                DataConfigScreen("Data Config", listOf("Permission 1", "Permission 2"))
            }
        }
    }
}