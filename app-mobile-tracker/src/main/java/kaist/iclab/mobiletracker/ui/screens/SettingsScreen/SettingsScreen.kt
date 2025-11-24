package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.ui.theme.AppColors

private val CARD_HORIZONTAL_PADDING = 16.dp
private val CARD_VERTICAL_PADDING = 16.dp
private val CARD_CORNER_RADIUS = 12.dp

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
            contentPadding = PaddingValues(vertical = CARD_VERTICAL_PADDING)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = CARD_HORIZONTAL_PADDING),
                    colors = CardDefaults.cardColors(containerColor = AppColors.White),
                    shape = RoundedCornerShape(CARD_CORNER_RADIUS)
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

@Composable
private fun SettingsMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = CARD_HORIZONTAL_PADDING, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.Filled.ChevronRight,
            contentDescription = null,
            tint = AppColors.TextSecondary
        )
    }
}
