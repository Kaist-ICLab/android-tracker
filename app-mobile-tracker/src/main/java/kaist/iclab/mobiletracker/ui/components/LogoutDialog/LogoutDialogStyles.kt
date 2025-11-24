package kaist.iclab.mobiletracker.ui.components.LogoutDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * LogoutDialog style constants
 * Style definitions specific to LogoutDialog component
 */
object LogoutDialogStyles {
    // Dialog container
    val DialogContainerColor = Color.White
    val DialogCornerRadius = 8.dp
    val DialogShape = RoundedCornerShape(DialogCornerRadius)
    
    // Title style
    val TitleFontSize = 18.sp
    val TitleFontWeight = FontWeight.Bold
    val TitleColor = Color.Black
    
    // Message style
    val MessageFontSize = 14.sp
    val MessageColor = Color(0xFF6B7280) // Gray
    
    // Button dimensions
    val ButtonWidth = 120.dp
    val ButtonHeight = 40.dp
    
    // Confirm button style
    val ConfirmButtonColor = AppColors.PrimaryColor
    val ConfirmButtonTextColor = Color.White
    val ConfirmButtonTextSize = 14.sp
    val ConfirmButtonCornerRadius = 8.dp
    val ConfirmButtonShape = RoundedCornerShape(ConfirmButtonCornerRadius)
    
    // Dismiss button style
    val DismissButtonTextColor = Color.Black
    val DismissButtonTextSize = 14.sp
    val DismissButtonBorder = BorderStroke(1.dp, AppColors.BorderLight)
    val DismissButtonCornerRadius = 4.dp
    val DismissButtonShape = RoundedCornerShape(DismissButtonCornerRadius)
}

