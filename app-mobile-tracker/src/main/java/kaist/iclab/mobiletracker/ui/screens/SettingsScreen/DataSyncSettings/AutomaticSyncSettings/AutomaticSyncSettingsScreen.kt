package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.AutomaticSyncSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.screens.SettingsScreen.DataSyncSettings.AutomaticSyncSettings.Styles
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Automatic Sync Settings screen
 */
@Composable
fun AutomaticSyncSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Styles.HEADER_HEIGHT)
                    .padding(horizontal = Styles.HEADER_HORIZONTAL_PADDING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = context.getString(R.string.sync_automatic_sync),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.TITLE_FONT_SIZE
                )
            }

            // Main content - scrollable
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(
                        PaddingValues(
                            start = Styles.CARD_CONTAINER_HORIZONTAL_PADDING,
                            top = 8.dp,
                            end = Styles.CARD_CONTAINER_HORIZONTAL_PADDING,
                            bottom = Styles.CARD_VERTICAL_PADDING
                        )
                    )
            ) {
                // Placeholder content
                Text(
                    text = "Automatic Sync settings will be implemented here",
                    fontSize = Styles.SECTION_DESCRIPTION_FONT_SIZE,
                    color = AppColors.TextSecondary
                )
            }
        }
    }
}

