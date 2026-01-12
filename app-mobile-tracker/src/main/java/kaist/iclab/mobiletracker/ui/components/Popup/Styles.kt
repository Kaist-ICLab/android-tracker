package kaist.iclab.mobiletracker.ui.components.Popup

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * PopupDialog style constants
 * Style definitions specific to PopupDialog component
 */
object Styles {
    // Dialog container
    val ContainerColor = Color.White
    val MaxWidth = 300.dp
    val CardPadding = 24.dp
    val CardCornerRadius = 8.dp
    
    // Title styles
    val TitleFontSize = 18.sp
    val TitleFontWeight = FontWeight.Bold
    val TitleColor = Color.Black
    val TitleBottomPadding = 16.dp
    
    // Button styles
    val ButtonDefaultWidth = 120.dp
    val ButtonDefaultHeight = 40.dp
    val ButtonTextFontSize = 14.sp
    val ButtonSpacing = 12.dp
    val ButtonCornerRadius = RoundedCornerShape(8.dp)
    val OutlinedButtonCornerRadius = RoundedCornerShape(4.dp)
    val ButtonBorderWidth = 1.dp
    val ButtonBorderColor = AppColors.BorderLight
    
    // Spacing
    val ContentButtonSpacing = 24.dp
}