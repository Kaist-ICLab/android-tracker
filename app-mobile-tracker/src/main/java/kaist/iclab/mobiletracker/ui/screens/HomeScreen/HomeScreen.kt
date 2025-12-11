package kaist.iclab.mobiletracker.ui.screens.HomeScreen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.ui.screens.HomeScreen.Styles

/**
 * Home screen - placeholder for future implementation
 */
@Composable
fun HomeScreen() {
    val context = LocalContext.current
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = context.getString(R.string.home_screen),
            fontSize = Styles.PLACEHOLDER_FONT_SIZE,
            color = AppColors.TextPrimary
        )
    }
}

