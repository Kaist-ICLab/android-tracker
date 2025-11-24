package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PhoneSensorSettings

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * PhoneSensor style constants
 * Centralized style definitions for PhoneSensor components
 */
object Styles {
    // Card
    val CARD_CORNER_RADIUS = 20.dp
    val CARD_VERTICAL_PADDING = 4.dp
    val CARD_HORIZONTAL_PADDING = 16.dp
    val CARD_CONTAINER_HORIZONTAL_PADDING = 10.dp

    // Container shape that wraps all cards
    val CONTAINER_SHAPE = RoundedCornerShape(CARD_CORNER_RADIUS)
    
    // Individual card shape (no rounding, since container handles it)
    val CARD_SHAPE = RoundedCornerShape(0.dp)

    // Typography
    val TEXT_FONT_SIZE = 15.sp
    val DESCRIPTION_FONT_SIZE = 12.sp

    // Text Padding
    val TEXT_TOP_PADDING = 3.dp
    val DESCRIPTION_BOTTOM_PADDING = 3.dp

    // Spacing
    val SPACER_WIDTH = 10.dp
    val ICON_SPACER_WIDTH = 15.dp
    val SETTING_CONTAINER_BOTTOM_PADDING = 16.dp

    // Icon
    val ICON_SIZE = 24.dp

    // Switch
    const val SWITCH_SCALE = 0.7f

    @Composable
    fun switchColors() = SwitchDefaults.colors(
        checkedThumbColor = AppColors.White,
        checkedTrackColor = AppColors.NavigationBarSelected,
        uncheckedThumbColor = AppColors.White,
        uncheckedTrackColor = AppColors.TextSecondary,
        checkedBorderColor = Color.Transparent,
        uncheckedBorderColor = Color.Transparent,
        disabledCheckedThumbColor = AppColors.White,
        disabledCheckedTrackColor = AppColors.TextSecondary,
        disabledUncheckedThumbColor = AppColors.White,
        disabledUncheckedTrackColor = AppColors.TextSecondary
    )
}