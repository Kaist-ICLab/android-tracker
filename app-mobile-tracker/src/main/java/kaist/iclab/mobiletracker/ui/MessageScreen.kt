package kaist.iclab.mobiletracker.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Message screen - placeholder for future implementation
 */
@Composable
fun MessageScreen() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Message Screen",
            fontSize = 24.sp,
            color = AppColors.TextPrimary
        )
    }
}

