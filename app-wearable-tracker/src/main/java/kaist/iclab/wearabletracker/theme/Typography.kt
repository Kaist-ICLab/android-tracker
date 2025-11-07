package kaist.iclab.wearabletracker.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * Typography definitions
 * Provides reusable text styles throughout the app
 */
object AppTypography {
    val deviceName: TextStyle
        get() = TextStyle(
            fontSize = 10.sp,
            fontWeight = FontWeight.Normal
        )
    
    val syncStatus: TextStyle
        get() = TextStyle(
            fontSize = 9.sp,
            fontWeight = FontWeight.Normal
        )
    
    val sensorName: TextStyle
        get() = TextStyle(
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal
        )
    
    val dialogTitle: TextStyle
        get() = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
    
    val dialogBody: TextStyle
        get() = TextStyle(
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal
        )
}

