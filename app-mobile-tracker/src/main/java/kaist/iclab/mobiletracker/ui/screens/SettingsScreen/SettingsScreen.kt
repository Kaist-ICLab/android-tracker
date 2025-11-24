package kaist.iclab.mobiletracker.ui.screens.SettingsScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.helpers.LanguageHelper
import kaist.iclab.mobiletracker.navigation.Screen
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Settings screen with menu items
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val context = LocalContext.current
    val languageHelper = remember { LanguageHelper(context) }
    val currentLanguage = languageHelper.getLanguage()
    
    // Get current language display name
    val currentLanguageDisplayName = when (currentLanguage) {
        "ko" -> context.getString(R.string.language_korean_full)
        "en" -> context.getString(R.string.language_english_full)
        else -> context.getString(R.string.language_english_full)
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = context.getString(R.string.nav_settings),
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(top = 8.dp, bottom = Styles.CARD_VERTICAL_PADDING)
            ) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = Styles.CARD_CONTAINER_HORIZONTAL_PADDING),
                        colors = CardDefaults.cardColors(containerColor = AppColors.White),
                        shape = Styles.CARD_SHAPE
                    ) {
                        SettingsMenuItem(
                            title = context.getString(R.string.menu_language),
                            icon = Icons.Filled.Language,
                            description = currentLanguageDisplayName,
                            onClick = { navController.navigate(Screen.Language.route) }
                        )
                        HorizontalDivider(
                            color = AppColors.BorderDark,
                            thickness = 0.dp
                        )
                        SettingsMenuItem(
                            title = context.getString(R.string.menu_phone_sensor),
                            icon = Icons.Filled.PhoneAndroid,
                            onClick = { navController.navigate(Screen.PhoneSensor.route) }
                        )
                    }
                }
            }
        }
    }
}
