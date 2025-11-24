package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Settings screen with menu items
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.nav_settings),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = Styles.CARD_VERTICAL_PADDING)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Styles.CARD_CONTAINER_HORIZONTAL_PADDING),
                        colors = CardDefaults.cardColors(containerColor = AppColors.White),
                        shape = Styles.CARD_SHAPE
                    ) {
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_account),
                            icon = Icons.Filled.AccountBox,
                            onClick = { navController.navigate(Screen.Account.route) }
                        )
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_devices),
                            icon = Icons.Filled.Devices,
                            onClick = { navController.navigate(Screen.Devices.route) }
                        )
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_language),
                            icon = Icons.Filled.Language,
                            description = getLanguageDisplayName(context),
                            onClick = { navController.navigate(Screen.Language.route) }
                        )
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_permission),
                            icon = Icons.Filled.Security,
                            onClick = { navController.navigate(Screen.Permission.route) }
                        )
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_phone_sensor),
                            icon = Icons.Filled.PhoneAndroid,
                            onClick = { navController.navigate(Screen.PhoneSensor.route) }
                        )
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_server_sync),
                            icon = Icons.Filled.CloudSync,
                            onClick = { navController.navigate(Screen.ServerSync.route) }
                        )
                        SettingsMenuItemWithDivider(
                            title = context.getString(R.string.menu_about),
                            icon = Icons.Filled.Info,
                            onClick = { navController.navigate(Screen.About.route) },
                            showDivider = false
                        )
                    }
                }
            }
        }
    }
}
