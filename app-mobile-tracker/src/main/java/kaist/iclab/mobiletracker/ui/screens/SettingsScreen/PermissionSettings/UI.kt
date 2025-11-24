package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PermissionSettings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.tracker.permission.Permission
import kaist.iclab.tracker.permission.PermissionState

@Composable
fun PermissionCard(
    modifier: Modifier = Modifier,
    permission: Permission,
    permissionState: PermissionState,
    onRequest: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.Transparent),
        shape = Styles.CARD_SHAPE
    ) {
        PermissionRow(
            permission = permission,
            permissionState = permissionState,
            onRequest = onRequest
        )
    }
}

@Composable
private fun PermissionRow(
    permission: Permission,
    permissionState: PermissionState,
    onRequest: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Use Permission's built-in name and description
    val permissionName = permission.name
    val permissionDescription = permission.description
    // Get icon from first permission ID
    val permissionIcon = getPermissionIcon(permission.ids.first())
    
    // Status text and color
    val (statusText, statusColor) = when (permissionState) {
        PermissionState.PERMANENTLY_DENIED -> "Denied" to AppColors.ErrorColor
        PermissionState.UNSUPPORTED -> "Device not supported" to AppColors.ErrorColor
        PermissionState.NOT_REQUESTED -> "Waiting to be Granted" to AppColors.TextPrimary
        PermissionState.GRANTED -> "Granted" to AppColors.PrimaryColor
        PermissionState.RATIONALE_REQUIRED -> "Not fully granted" to AppColors.ErrorColor
    }
    
    // Show arrow only if not unsupported
    val showArrow = permissionState != PermissionState.UNSUPPORTED
    // Make card clickable only if not unsupported
    val isClickable = permissionState != PermissionState.UNSUPPORTED

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isClickable) {
                    Modifier.clickable { onRequest() }
                } else {
                    Modifier
                }
            )
            .padding(
                horizontal = Styles.CARD_HORIZONTAL_PADDING,
                vertical = Styles.CARD_VERTICAL_PADDING
            )
    ) {
        Icon(
            imageVector = permissionIcon,
            contentDescription = permissionName,
            modifier = Modifier.size(Styles.ICON_SIZE),
            tint = AppColors.PrimaryColor
        )
        Spacer(Modifier.width(Styles.ICON_SPACER_WIDTH))
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = permissionName,
                color = AppColors.TextPrimary,
                fontSize = Styles.TEXT_FONT_SIZE,
                modifier = Modifier.padding(top = Styles.TEXT_TOP_PADDING)
            )
            Text(
                text = statusText,
                color = statusColor,
                fontSize = Styles.STATUS_TEXT_FONT_SIZE,
                modifier = Modifier.padding(top = Styles.STATUS_TOP_PADDING)
            )
            Text(
                text = permissionDescription,
                color = AppColors.TextSecondary,
                fontSize = Styles.CARD_DESCRIPTION_FONT_SIZE,
                modifier = Modifier.padding(bottom = Styles.CARD_DESCRIPTION_BOTTOM_PADDING)
            )
        }
        if (showArrow) {
            Spacer(Modifier.width(Styles.SPACER_WIDTH))
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = AppColors.TextSecondary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

