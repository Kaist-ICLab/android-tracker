package kaist.iclab.field_tracker.ui

import android.icu.text.NumberFormat
import android.util.Log
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
import kaist.iclab.tracker.collector.core.Collector
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

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
                collectorMap = viewModel.collectors,
                permissionMap = permissionState.value,
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
            val collector: Collector =
                viewModel.collectors.get(name) ?: error("Collector is null")
            Log.d("MainApp", "Name: $name")
            val dataStorage = viewModel.dataStorages.get(name) ?: error("DataStorage is null")
            val stat =  dataStorage.statFlow.collectAsState().value

            DataConfigScreen(
                canNavigateBack = navController.previousBackStackEntry != null,
                navigateBack = { navController.popBackStack() },
                collector = collector.toCollectorData(),
                permissionMap = permissionState.value.filter { it.key in collector.permissions },
                onPermissionRequest = { names,onResult -> viewModel.requestPermissions(names, onResult) },
                recordCount = NumberFormat.getNumberInstance(Locale.US).format(stat.timestamp),
                lastUpdated = convertUnixToFormatted(stat.count),
            )
        }

    }
}


fun convertUnixToFormatted(timestampMs: Long): String {
    val date = Date(timestampMs)
    val sdf = SimpleDateFormat("yyyy:MM:dd HH:mm:ss.SSS", Locale.getDefault())
    sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul") // UTC+0900
    return sdf.format(date) + " (UTC+0900)"
}


