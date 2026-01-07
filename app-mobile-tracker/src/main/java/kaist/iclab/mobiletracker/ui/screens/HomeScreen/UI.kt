package kaist.iclab.mobiletracker.ui.screens.HomeScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.theme.AppColors

@Composable
fun TrackingStatusCard(
    isActive: Boolean,
    lastSyncedTime: String?,
    onSyncClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = Styles.STATUS_CARD_SHAPE,
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Styles.STATUS_CARD_ELEVATION)
    ) {
        Row(
            modifier = Modifier
                .padding(Styles.STATUS_CARD_PADDING)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(R.string.home_tracking_status),
                        fontSize = Styles.STATUS_TITLE_FONT_SIZE,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.TextPrimary
                    )
                    Spacer(modifier = Modifier.width(Styles.SCREEN_VERTICAL_SPACING))
                    StatusIndicator(isActive = isActive)
                }
                Text(
                    text = if (lastSyncedTime != null) stringResource(R.string.home_last_synced, lastSyncedTime) else stringResource(R.string.home_never_synced),
                    fontSize = Styles.STATUS_SUBTITLE_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(top = Styles.STATUS_SUBTITLE_TOP_PADDING)
                )
            }

            IconButton(
                onClick = onSyncClick,
                modifier = Modifier
                    .size(Styles.STATUS_ICON_BUTTON_SIZE)
                    .background(AppColors.PrimaryColor.copy(alpha = 0.1f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Sync,
                    contentDescription = "Sync Now",
                    tint = AppColors.PrimaryColor
                )
            }
        }
    }
}

@Composable
fun StatusIndicator(isActive: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(
                color = if (isActive) Styles.Colors.RUNNING_BG else Styles.Colors.STOPPED_BG,
                shape = Styles.INDICATOR_SHAPE
            )
            .padding(horizontal = Styles.INDICATOR_HORIZONTAL_PADDING, vertical = Styles.INDICATOR_VERTICAL_PADDING)
    ) {
        Box(
            modifier = Modifier
                .size(Styles.INDICATOR_DOT_SIZE)
                .background(
                    color = if (isActive) Styles.Colors.RUNNING_DOT else Styles.Colors.STOPPED_DOT,
                    shape = CircleShape
                )
        )
        Spacer(modifier = Modifier.width(Styles.TOP_SPACER_HEIGHT))
        Text(
            text = if (isActive) stringResource(R.string.home_status_running) else stringResource(R.string.home_status_stopped),
            fontSize = Styles.INDICATOR_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = if (isActive) Styles.Colors.RUNNING_TEXT else Styles.Colors.STOPPED_TEXT
        )
    }
}

@Composable
fun InsightCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        modifier = modifier.fillMaxHeight(),
        shape = Styles.INSIGHT_CARD_SHAPE,
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = Styles.INSIGHT_CARD_ELEVATION)
    ) {
        Column(
            modifier = Modifier.padding(Styles.INSIGHT_CARD_PADDING).fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) { 
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(Styles.INSIGHT_ICON_SIZE)
            )
            Column(modifier = Modifier.padding(top = Styles.INSIGHT_CONTENT_TOP_PADDING)) {
                Text(
                    text = value,
                    fontSize = Styles.INSIGHT_VALUE_FONT_SIZE,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = title,
                    fontSize = Styles.INSIGHT_LABEL_FONT_SIZE,
                    color = AppColors.TextSecondary,
                    modifier = Modifier.padding(top = Styles.INSIGHT_LABEL_TOP_PADDING),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
