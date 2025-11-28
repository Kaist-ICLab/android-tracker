package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.AccountSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.components.LogoutDialog.LogoutDialog
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.auth.AuthViewModel
import org.koin.androidx.compose.koinViewModel

/**
 * Account settings screen
 * Displays user information similar to About phone screen
 */
@Composable
fun AccountSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val userState by authViewModel.userState.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Logout confirmation dialog
    if (showLogoutDialog) {
        LogoutDialog(
            onDismiss = { showLogoutDialog = false },
            onConfirm = {
                authViewModel.logout()
                showLogoutDialog = false
            }
        )
    }

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
                    text = context.getString(R.string.menu_account),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.TITLE_FONT_SIZE
                )
            }

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = Styles.CONTENT_TOP_PADDING),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // User name
                Text(
                    text = userState.user?.name ?: context.getString(R.string.unknown),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.NAME_FONT_SIZE,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(Styles.BUTTON_TOP_PADDING))

                // Sign out button (replaces Rename button)
                Button(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .fillMaxWidth(Styles.BUTTON_WIDTH_RATIO),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.BorderLight,
                        contentColor = AppColors.TextPrimary
                    ),
                    shape = RoundedCornerShape(Styles.BUTTON_CORNER_RADIUS)
                ) {
                    Text(
                        text = context.getString(R.string.sign_out),
                        fontSize = Styles.BUTTON_TEXT_FONT_SIZE
                    )
                }

                Spacer(modifier = Modifier.height(Styles.INFO_TOP_PADDING))

                // User information list
                Column(
                    modifier = Modifier
                        .fillMaxWidth(Styles.INFO_CARD_WIDTH_RATIO)
                        .padding(horizontal = Styles.INFO_CARD_PADDING)
                ) {
                    // Email row
                    InfoRow(
                        label = context.getString(R.string.email_label),
                        value = userState.user?.email ?: context.getString(R.string.no_email)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = Styles.INFO_ROW_VERTICAL_PADDING),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = Styles.INFO_LABEL_FONT_SIZE,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            fontSize = Styles.INFO_VALUE_FONT_SIZE,
            color = AppColors.TextPrimary
        )
    }
}
