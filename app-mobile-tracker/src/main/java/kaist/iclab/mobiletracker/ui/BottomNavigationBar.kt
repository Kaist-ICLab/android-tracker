package kaist.iclab.mobiletracker.ui

import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Info
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.NavHostController
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Bottom navigation bar with four tabs: Home, Data, Messages, and Settings
 * Following Android official documentation: https://developer.android.com/develop/ui/compose/components/navigation-bar
 */
@Composable
fun BottomNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    // Define destinations
    val destinations = listOf(
        Destination(Screen.Home.route, "Home", Icons.Outlined.Home, Icons.Filled.Home),
        Destination(Screen.Data.route, "Data", Icons.Outlined.Info, Icons.Filled.Info),
        Destination(Screen.Message.route, "Messages", Icons.Outlined.Email, Icons.Filled.Email),
        Destination(Screen.Setting.route, "Settings", Icons.Outlined.Settings, Icons.Filled.Settings)
    )
    
    // Observe current route changes using currentBackStackEntryAsState
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Find current destination index
    val currentIndex = destinations.indexOfFirst { it.route == currentRoute }
    
    // Manage selected destination state using rememberSaveable
    // Initialize with current route index if found, otherwise default to 0
    var selectedDestination by rememberSaveable {
        mutableIntStateOf(if (currentIndex >= 0) currentIndex else 0)
    }
    
    // Sync selectedDestination with current route when route changes
    LaunchedEffect(currentRoute) {
        if (currentIndex >= 0) {
            selectedDestination = currentIndex
        }
    }

    NavigationBar(
        modifier = modifier.height(110.dp),
        windowInsets = NavigationBarDefaults.windowInsets,
        containerColor = AppColors.NavigationBarBackground
    ) {
        destinations.forEachIndexed { index, destination ->
            NavigationBarItem(
                selected = selectedDestination == index,
                onClick = {
                    selectedDestination = index
                    navController.navigate(route = destination.route) {
                        // Pop up to the start destination to avoid building up a back stack
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (selectedDestination == index) {
                            destination.filledIcon
                        } else {
                            destination.outlinedIcon
                        },
                        contentDescription = destination.label
                    )
                },
                label = { Text(destination.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AppColors.NavigationBarSelected,
                    selectedTextColor = AppColors.NavigationBarSelected,
                    indicatorColor = AppColors.Transparent,
                    unselectedIconColor = AppColors.NavigationBarUnselected,
                    unselectedTextColor = AppColors.NavigationBarUnselected
                )
            )
        }
    }
}

private data class Destination(
    val route: String,
    val label: String,
    val outlinedIcon: androidx.compose.ui.graphics.vector.ImageVector,
    val filledIcon: androidx.compose.ui.graphics.vector.ImageVector
)

