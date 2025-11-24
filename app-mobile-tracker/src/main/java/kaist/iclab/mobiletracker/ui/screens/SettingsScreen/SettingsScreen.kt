package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = Styles.CARD_VERTICAL_PADDING)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Styles.CARD_HORIZONTAL_PADDING),
                    colors = CardDefaults.cardColors(containerColor = AppColors.White),
                    shape = Styles.CARD_SHAPE
                ) {
                    SettingsMenuItem(
                        title = context.getString(R.string.menu_language),
                        onClick = { navController.navigate(Screen.Language.route) }
                    )
                    HorizontalDivider(
                        color = AppColors.BorderDark,
                        thickness = 0.dp
                    )
                    SettingsMenuItem(
                        title = context.getString(R.string.menu_phone_sensor),
                        onClick = { navController.navigate(Screen.PhoneSensor.route) }
                    )
                }
            }
        }
    }
}
