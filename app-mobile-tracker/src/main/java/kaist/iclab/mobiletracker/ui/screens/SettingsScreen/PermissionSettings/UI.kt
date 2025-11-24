package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PermissionSettings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    
    val isButtonEnabled = permissionState != PermissionState.UNSUPPORTED && 
                          permissionState != PermissionState.GRANTED
    val buttonText = when (permissionState) {
        PermissionState.UNSUPPORTED -> "Hardware Not Available"
        PermissionState.GRANTED -> "Already Granted"
        PermissionState.NOT_REQUESTED -> "Request Permission"
        PermissionState.RATIONALE_REQUIRED -> "Request Permission"
        PermissionState.PERMANENTLY_DENIED -> "Open Settings"
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
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
                text = permissionDescription,
                color = AppColors.TextSecondary,
                fontSize = Styles.CARD_DESCRIPTION_FONT_SIZE,
                modifier = Modifier.padding(bottom = Styles.CARD_DESCRIPTION_BOTTOM_PADDING)
            )
        }
        Spacer(Modifier.width(Styles.SPACER_WIDTH))
        PermissionButton(
            text = buttonText,
            enabled = isButtonEnabled,
            onClick = onRequest,
            permissionState = permissionState
        )
    }
}

@Composable
private fun PermissionButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    permissionState: PermissionState
) {
    val buttonColors = when (permissionState) {
        PermissionState.GRANTED -> ButtonDefaults.buttonColors(
            containerColor = AppColors.PrimaryColor.copy(alpha = 0.5f),
            contentColor = AppColors.White
        )
        PermissionState.PERMANENTLY_DENIED -> ButtonDefaults.buttonColors(
            containerColor = androidx.compose.ui.graphics.Color(0xFFDC2626), // Red color for denied state
            contentColor = AppColors.White
        )
        else -> ButtonDefaults.buttonColors(
            containerColor = AppColors.PrimaryColor,
            contentColor = AppColors.White
        )
    }
    
    Button(
        onClick = onClick,
        enabled = enabled,
        colors = buttonColors,
        modifier = Modifier.padding(0.dp)
    ) {
        Text(
            text = text,
            fontSize = Styles.BUTTON_TEXT_FONT_SIZE
        )
    }
}
