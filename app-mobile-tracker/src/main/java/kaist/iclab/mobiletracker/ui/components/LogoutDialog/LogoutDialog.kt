package kaist.iclab.mobiletracker.ui.components.LogoutDialog

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.DialogProperties
import kaist.iclab.mobiletracker.R

/**
 * Logout confirmation dialog
 */
@Composable
fun LogoutDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = context.getString(R.string.logout_title),
                fontSize = LogoutDialogStyles.TitleFontSize,
                fontWeight = LogoutDialogStyles.TitleFontWeight,
                color = LogoutDialogStyles.TitleColor
            )
        },
        text = {
            Text(
                text = context.getString(R.string.logout_message),
                fontSize = LogoutDialogStyles.MessageFontSize,
                color = LogoutDialogStyles.MessageColor,
                textAlign = TextAlign.Start
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onConfirm()
                },
                modifier = Modifier
                    .width(LogoutDialogStyles.ButtonWidth)
                    .height(LogoutDialogStyles.ButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = LogoutDialogStyles.ConfirmButtonColor
                ),
                shape = LogoutDialogStyles.ConfirmButtonShape
            ) {
                Text(
                    text = context.getString(R.string.logout_confirm),
                    color = LogoutDialogStyles.ConfirmButtonTextColor,
                    fontSize = LogoutDialogStyles.ConfirmButtonTextSize
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .width(LogoutDialogStyles.ButtonWidth)
                    .height(LogoutDialogStyles.ButtonHeight),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = LogoutDialogStyles.DismissButtonTextColor
                ),
                border = LogoutDialogStyles.DismissButtonBorder,
                shape = LogoutDialogStyles.DismissButtonShape
            ) {
                Text(
                    text = context.getString(R.string.logout_close),
                    fontSize = LogoutDialogStyles.DismissButtonTextSize
                )
            }
        },
        containerColor = LogoutDialogStyles.DialogContainerColor,
        shape = LogoutDialogStyles.DialogShape,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

