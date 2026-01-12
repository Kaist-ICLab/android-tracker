package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.WatchCollectedDataSettings

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.Styles as DataSyncStyles
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.Styles as CommonStyles

/**
 * Watch Collected Data Settings screen style constants
 * Uses common styles from DataSyncSettings.Styles and SettingsScreen.Styles
 */
object Styles {
    // Header (from DataSyncSettings)
    val HEADER_HEIGHT = DataSyncStyles.HEADER_HEIGHT
    val HEADER_HORIZONTAL_PADDING = DataSyncStyles.HEADER_HORIZONTAL_PADDING
    val TITLE_FONT_SIZE = DataSyncStyles.TITLE_FONT_SIZE
    
    // Sensor Card spacing (from DataSyncSettings)
    val SENSOR_CARD_SPACING = DataSyncStyles.SENSOR_CARD_SPACING
    
    // Screen Description (from CommonStyles)
    val SCREEN_DESCRIPTION_FONT_SIZE = CommonStyles.SCREEN_DESCRIPTION_FONT_SIZE
    val SCREEN_DESCRIPTION_HORIZONTAL_PADDING = CommonStyles.SCREEN_DESCRIPTION_HORIZONTAL_PADDING
    val SCREEN_DESCRIPTION_BOTTOM_PADDING = CommonStyles.SCREEN_DESCRIPTION_BOTTOM_PADDING
    
    // Content padding (from CommonStyles)
    val CARD_CONTAINER_HORIZONTAL_PADDING = CommonStyles.CARD_CONTAINER_HORIZONTAL_PADDING
    val CARD_VERTICAL_PADDING = CommonStyles.CARD_VERTICAL_PADDING
}

