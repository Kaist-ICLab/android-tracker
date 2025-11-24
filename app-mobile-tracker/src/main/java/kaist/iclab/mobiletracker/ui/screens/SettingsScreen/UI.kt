package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kaist.iclab.mobiletracker.ui.theme.AppColors

@Composable
fun SettingsMenuItem(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(
                horizontal = Styles.MENU_ITEM_HORIZONTAL_PADDING,
                vertical = Styles.MENU_ITEM_VERTICAL_PADDING
            ),
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
