package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Settings screen style constants
 * Centralized style definitions for Settings components
 */
object Styles {
    // Header
    val HEADER_HEIGHT = 56.dp
    val HEADER_START_PADDING = 16.dp
    val HEADER_END_PADDING = 8.dp
    val HEADER_FONT_SIZE = 22.sp
    
    // Card
    val CARD_HORIZONTAL_PADDING = 16.dp
    val CARD_VERTICAL_PADDING = 16.dp
    val CARD_CORNER_RADIUS = 20.dp
    val CARD_CONTAINER_HORIZONTAL_PADDING = 10.dp
    val CARD_SHAPE = RoundedCornerShape(CARD_CORNER_RADIUS)
    val CARD_SPACING = 10.dp
    
    // Menu item
    val MENU_ITEM_HORIZONTAL_PADDING = 16.dp
    val MENU_ITEM_VERTICAL_PADDING = 16.dp
    
    // Enable Tracker Card
    val ENABLE_TRACKER_VERTICAL_PADDING = 12.dp
    
    // Icon
    val ICON_SIZE = 24.dp
    val ICON_SPACER_WIDTH = 15.dp
    
    // Typography
    val TEXT_FONT_SIZE = 15.sp
    val TEXT_LINE_HEIGHT = 18.sp
    val TEXT_TOP_PADDING = 3.dp
    val DESCRIPTION_FONT_SIZE = 12.sp
    val DESCRIPTION_LINE_HEIGHT = 15.sp
    val DESCRIPTION_TOP_PADDING = 4.dp
    
    // LazyColumn
    val LAZY_COLUMN_TOP_PADDING = 8.dp
}
