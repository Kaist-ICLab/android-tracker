package kaist.iclab.field_tracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import kaist.iclab.field_tracker.ui.screens.DataConfigScreen
import kaist.iclab.field_tracker.ui.screens.LoginScreen
import kaist.iclab.field_tracker.ui.screens.PermissionListScreen
import kaist.iclab.field_tracker.ui.screens.SettingScreen
import kaist.iclab.field_tracker.ui.screens.UserProfileScreen
import kaist.iclab.field_tracker.ui.screens.toCollectorData
import kaist.iclab.tracker.collector.core.CollectorInterface

enum class AppScreens(name: String) {
    Login("Login"),
    Setting("Setting"),
    PermissionList("PermissionList"),
    UserProfile("UserProfile"),
    DataConfig("DataConfig"),
}

@Composable
fun MainApp(
    navController: NavHostController,
    viewModel: AbstractMainViewModel
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()

    // Get the name of the current screen
    //  TODO:  val initialScreen = AppScreens.Login.name
    val initialScreen = AppScreens.Setting.name
//    val initialScreen = AppScreens.PermissionList.name
    val trackerState = viewModel.trackerStateFlow.collectAsState()

    val collectorState = viewModel.collectorStateFlow.collectAsState()

    val userState = viewModel.userStateFlow.collectAsState()
    val permissionState = viewModel.permissionStateFlow.collectAsState()


    NavHost(
        navController = navController,
        startDestination = initialScreen,
        modifier = Modifier
            .fillMaxSize()
    ) {
        composable(route = AppScreens.Login.name) {
            LoginScreen(
                onLogin = { viewModel.login() },
                onTestWithoutLogin = {
                    navController.navigate(AppScreens.Setting.name) {
                        popUpTo(0) { inclusive = true } // Remove all backstack
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(route = AppScreens.Setting.name) {
            SettingScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateBack = { navController.popBackStack() },
                onNavigateToPermissionList = { navController.navigate(AppScreens.PermissionList.name) },
                onNavigateToUserProfile = { navController.navigate(AppScreens.UserProfile.name) },
                onNavigateToDataConfig = { navController.navigate("${AppScreens.DataConfig.name}/${it}") },
                trackerState = trackerState.value,
                onTrackerStateChange = { if (it) viewModel.runTracker() else viewModel.stopTracker() },
                collectorMap = collectorState.value,
                enableCollector = { viewModel.enableCollector(it) },
                disableCollector = { viewModel.disableCollector(it) },
                userState = userState.value,
                deviceInfo = viewModel.getDeviceInfo(),
                appVersion = viewModel.getAppVersion()
            )
        }
        composable(route = AppScreens.PermissionList.name) {
            PermissionListScreen(
                permissionMap = permissionState.value,
                onPermissionRequest = { names -> viewModel.requestPermissions(names) },
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = AppScreens.UserProfile.name) {
            UserProfileScreen(
                user = userState.value.user ?: error("User is null"),
                logout = { viewModel.logout() },
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = "${AppScreens.DataConfig.name}/{data}", arguments = listOf(
            navArgument("data") { type = NavType.StringType }
        )) { backStackEntry ->
            val name = backStackEntry.arguments?.getString("data") ?: error("Name is null")
            val collector: CollectorInterface =
                viewModel.collectors.get(name) ?: error("Collector is null")
            DataConfigScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateBack = { navController.popBackStack() },
                collector = collector.toCollectorData(),
                permissionMap = permissionState.value.filter { it.key in collector.permissions },
                onPermissionRequest = { names,onResult -> viewModel.requestPermissions(names, onResult) },
            )
        }
    }
}



