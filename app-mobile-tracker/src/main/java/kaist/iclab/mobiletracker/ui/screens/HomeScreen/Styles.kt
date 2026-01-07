package kaist.iclab.mobiletracker.ui.screens.HomeScreen

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Home screen style constants
 */
object Styles {
    // Layout spacings
    val SCREEN_HORIZONTAL_PADDING = 16.dp
    val SCREEN_VERTICAL_SPACING = 8.dp
    val TOP_SPACER_HEIGHT = 4.dp
    val BOTTOM_SPACER_HEIGHT = 8.dp
    
    // Greeting Section
    val GREETING_TITLE_FONT_SIZE = 22.sp
    val GREETING_SUBTITLE_FONT_SIZE = 14.sp
    val GREETING_SUBTITLE_TOP_PADDING = 4.dp
    
    // Grid Spacing
    val GRID_VERTICAL_SPACING = 8.dp
    val GRID_HORIZONTAL_SPACING = 10.dp
    val GRID_SECTION_TITLE_FONT_SIZE = 16.sp
    val GRID_SECTION_TITLE_TOP_PADDING = 2.dp

    // Tracking Status Card
    val STATUS_CARD_CORNER_RADIUS = 16.dp
    val STATUS_CARD_SHAPE = RoundedCornerShape(STATUS_CARD_CORNER_RADIUS)
    val STATUS_CARD_ELEVATION = 0.dp
    val STATUS_CARD_PADDING = 14.dp
    val STATUS_TITLE_FONT_SIZE = 17.sp
    val STATUS_SUBTITLE_FONT_SIZE = 13.sp
    val STATUS_SUBTITLE_TOP_PADDING = 2.dp
    val STATUS_ICON_BUTTON_SIZE = 44.dp
    
    // Status Indicator (Tag)
    val INDICATOR_CORNER_RADIUS = 10.dp
    val INDICATOR_SHAPE = RoundedCornerShape(INDICATOR_CORNER_RADIUS)
    val INDICATOR_HORIZONTAL_PADDING = 6.dp
    val INDICATOR_VERTICAL_PADDING = 3.dp
    val INDICATOR_DOT_SIZE = 6.dp
    val INDICATOR_FONT_SIZE = 11.sp

    // Insight Card
    val INSIGHT_CARD_CORNER_RADIUS = 16.dp
    val INSIGHT_CARD_SHAPE = RoundedCornerShape(INSIGHT_CARD_CORNER_RADIUS)
    val INSIGHT_CARD_ELEVATION = 0.dp
    val INSIGHT_CARD_PADDING = 12.dp
    val INSIGHT_ICON_SIZE = 24.dp
    val INSIGHT_VALUE_FONT_SIZE = 16.sp
    val INSIGHT_LABEL_FONT_SIZE = 13.sp
    val INSIGHT_LABEL_TOP_PADDING = 1.dp
    val INSIGHT_CONTENT_TOP_PADDING = 4.dp

    /**
     * Home screen color palette
     */
    object Colors {
        // Sensor Colors
        val LOCATION = Color(0xFF4285F4)
        val APP_USAGE = Color(0xFF9C27B0)
        val ACTIVITY = Color(0xFF34A853)
        val DEVICE_STATUS = Color(0xFFFBBC04)
        val NOTIFICATIONS = Color(0xFFEA4335)
        val SCREEN = Color(0xFF607D8B)
        val CONNECTIVITY = Color(0xFF00ACC1)
        val BLUETOOTH = Color(0xFF3F51B5)
        
        // Status Indicator
        val RUNNING_BG = Color(0xFFE6F4EA)
        val RUNNING_DOT = Color(0xFF34A853)
        val RUNNING_TEXT = Color(0xFF137333)
        
        val STOPPED_BG = Color(0xFFFCE8E8)
        val STOPPED_DOT = Color(0xFFEA4335)
        val STOPPED_TEXT = Color(0xFFC5221F)
    }
}
