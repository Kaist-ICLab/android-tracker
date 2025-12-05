package kaist.iclab.mobiletracker.ui.components.CampaignDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * CampaignDialog style constants
 * Style definitions specific to CampaignDialog component
 */
object CampaignDialogStyles {
    // Dialog container
    val DialogContainerColor = Color.White
    val DialogCornerRadius = 8.dp
    val DialogShape = RoundedCornerShape(DialogCornerRadius)
    
    // Title style
    val TitleFontSize = 18.sp
    val TitleFontWeight = FontWeight.Bold
    val TitleColor = Color.Black
    
    // Experiment name style
    val ExperimentNameFontSize = 14.sp
    val ExperimentNameColor = Color.Black
    
    // Button dimensions
    val ButtonWidth = 120.dp
    val ButtonHeight = 40.dp
    
    // Select button style
    val SelectButtonColor = AppColors.PrimaryColor
    val SelectButtonTextColor = Color.White
    val SelectButtonTextSize = 14.sp
    val SelectButtonCornerRadius = 8.dp
    val SelectButtonShape = RoundedCornerShape(SelectButtonCornerRadius)
    
    // Cancel button style
    val CancelButtonTextColor = Color.Black
    val CancelButtonTextSize = 14.sp
    val CancelButtonBorder = BorderStroke(1.dp, AppColors.BorderLight)
    val CancelButtonCornerRadius = 4.dp
    val CancelButtonShape = RoundedCornerShape(CancelButtonCornerRadius)
}

