package kaist.iclab.field_tracker.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.screens.DataConfigScreen
import kaist.iclab.field_tracker.ui.screens.LoginScreen
import kaist.iclab.field_tracker.ui.screens.PermissionListScreen
import kaist.iclab.field_tracker.ui.screens.SettingScreen
import kaist.iclab.field_tracker.ui.screens.UserProfileScreen
import kaist.iclab.tracker.collector.core.CollectorInterface

enum class AppScreens(name: String) {
    Login("login"),
    Setting("setting"),
    PermissionList("permission_list"),
    UserProfile("user_profile"),
    DataConfig("data_config"),
}

@Composable
fun MainApp(
    navController: NavHostController = rememberNavController(),
    viewModel: MainViewModelInterface
) {
    // Get current back stack entry
    val backStackEntry by navController.currentBackStackEntryAsState()
    // Get the name of the current screen
    val currentScreen = backStackEntry?.destination?.route ?: AppScreens.Login.name

    val trackerState = viewModel.trackerStateFlow.collectAsState()

    val collectorState = viewModel.collectorStateFlow.collectAsState()
    val collectorConfig = viewModel.collectorConfigFlow.collectAsState()
    val lastUpdate = viewModel.lastUpdatedFlow.collectAsState()
    val recordCount = viewModel.recordCountFlow.collectAsState()

    val userState = viewModel.userStateFlow.collectAsState()
    val permissionState = viewModel.permissionStateFlow.collectAsState()



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
                    onLogin = { viewModel.login() },
                    onTestWithoutLogin = { navController.navigate(AppScreens.Setting.name) }
                )
            }
            composable(route = AppScreens.Setting.name) {
                SettingScreen(
                    onNavigateToPermissionList = { navController.navigate(AppScreens.PermissionList.name) },
                    onNavigateToUserProfile = { navController.navigate(AppScreens.UserProfile.name) },
                    onNavigateToDataConfig = { data -> navController.navigate(AppScreens.DataConfig.name +"/"+ data) },
                    trackerState = trackerState.value,
                    onTrackerStateChange = { if(it) viewModel.runTracker() else viewModel.stopTracker() },
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
                    onPermissionStateChange = { name, isChecked -> viewModel.requestPermission(name) }
                )
            }
            composable(route = AppScreens.UserProfile.name) {
                UserProfileScreen(
                    user = userState.value.user?: error("User is null"),
                    logout = { viewModel.logout() }
                )
            }
            composable(route = AppScreens.DataConfig.name + "/{data}", arguments = listOf(
                navArgument("data") { type = NavType.StringType }
            )) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: error("Name is null")
                val collector: CollectorInterface = viewModel.collectors.get(name)?: error("Collector is null")
                DataConfigScreen(
                    collectorState = collectorState.value.get(name)?: error("CollectorState is null"),
                    permissionMap = permissionState.value.filter { it.key in collector.permissions},
                    collectorConfig = collectorConfig.value.get(name)?.toMap() ?: error("CollectorConfig is null"),
                    enableCollector = { viewModel.enableCollector(name) },
                    disableCollector = { viewModel.disableCollector(name) },
                    onPermissionStateChange = { name, isChecked -> viewModel.requestPermission(name) },
                    onConfigChange = { attr, value-> /*TODO - Modal로 Config Change*/ },
                    lastUpdated = lastUpdate.value.get(name)?: error("LastUpdate is null"),
                    recordCount = recordCount.value.get(name)?: error("RecordCount is null")
                )
            }
        }
    }
}