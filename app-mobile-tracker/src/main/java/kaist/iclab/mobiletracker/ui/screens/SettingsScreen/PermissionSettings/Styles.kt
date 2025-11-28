package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PermissionSettings

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Permission settings screen style constants
 * Follows the same pattern as PhoneSensorSettings for consistency
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

    // Header
    val HEADER_HEIGHT = 56.dp
    val HEADER_HORIZONTAL_PADDING = 8.dp
    val TITLE_FONT_SIZE = 22.sp
    
    // Screen Description (below main title)
    val SCREEN_DESCRIPTION_FONT_SIZE = 14.sp
    val SCREEN_DESCRIPTION_HORIZONTAL_PADDING = 30.dp
    val SCREEN_DESCRIPTION_BOTTOM_PADDING = 12.dp

    // Typography
    val TEXT_FONT_SIZE = 15.sp
    val TEXT_LINE_HEIGHT = 18.sp
    
    // Status text (between title and description)
    val STATUS_TEXT_FONT_SIZE = 13.sp
    val STATUS_TEXT_LINE_HEIGHT = 16.sp
    
    // Card Description (below card title)
    val CARD_DESCRIPTION_FONT_SIZE = 12.sp
    val CARD_DESCRIPTION_LINE_HEIGHT = 15.sp
    val CARD_DESCRIPTION_TOP_PADDING = 4.dp
    val CARD_DESCRIPTION_BOTTOM_PADDING = 3.dp

    // Text Padding
    val TEXT_TOP_PADDING = 3.dp
    val STATUS_TOP_PADDING = 3.dp

    // Spacing
    val SPACER_WIDTH = 10.dp
    val ICON_SPACER_WIDTH = 15.dp
    val SETTING_CONTAINER_BOTTOM_PADDING = 16.dp
    
    // Divider
    val DIVIDER_WIDTH_RATIO = 0.9f

    // Icon
    val ICON_SIZE = 24.dp
}

