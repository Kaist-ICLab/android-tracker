package kaist.iclab.mobiletracker.ui.components.Popup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Configuration for a dialog button
 */
data class DialogButtonConfig(
    val text: String,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
    val isPrimary: Boolean = true,
    val width: androidx.compose.ui.unit.Dp = 120.dp,
    val height: androidx.compose.ui.unit.Dp = 40.dp
)

/**
 * Reusable popup dialog component
 * 
 * @param title The title text of the dialog
 * @param content The content composable of the dialog
 * @param primaryButton Configuration for the primary button (usually on the right)
 * @param secondaryButton Configuration for the secondary button (usually on the left, optional)
 * @param onDismiss Callback when dialog is dismissed
 * @param containerColor Background color of the dialog
 * @param titleColor Color of the title text
 * @param titleFontSize Font size of the title
 * @param titleFontWeight Font weight of the title
 * @param centerButtons Whether to center the buttons horizontally
 * @param maxWidth Maximum width of the dialog (default: 280.dp for a more compact size)
 */
@Composable
fun PopupDialog(
    title: String,
    content: @Composable () -> Unit,
    primaryButton: DialogButtonConfig,
    secondaryButton: DialogButtonConfig? = null,
    onDismiss: () -> Unit,
    containerColor: Color = Color.White,
    titleColor: Color = Color.Black,
    titleFontSize: androidx.compose.ui.unit.TextUnit = 18.sp,
    titleFontWeight: FontWeight = FontWeight.Bold,
    centerButtons: Boolean = true,
    maxWidth: androidx.compose.ui.unit.Dp = 300.dp
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier.width(maxWidth),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = containerColor)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    // Title
                    Text(
                        text = title,
                        fontSize = titleFontSize,
                        fontWeight = titleFontWeight,
                        color = titleColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    
                    // Content
                    content()
                    
                    // Buttons
                    Spacer(modifier = Modifier.height(24.dp))
                    if (secondaryButton != null) {
                        // Two buttons: show both in a Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (centerButtons) Arrangement.Center else Arrangement.End
                        ) {
                            OutlinedButton(
                                onClick = secondaryButton.onClick,
                                enabled = secondaryButton.enabled,
                                modifier = Modifier
                                    .width(secondaryButton.width)
                                    .height(secondaryButton.height),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.Black
                                ),
                                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.BorderLight),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = secondaryButton.text,
                                    fontSize = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Button(
                                onClick = primaryButton.onClick,
                                enabled = primaryButton.enabled,
                                modifier = Modifier
                                    .width(primaryButton.width)
                                    .height(primaryButton.height),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (primaryButton.isPrimary) AppColors.PrimaryColor else Color.Gray
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = primaryButton.text,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    } else {
                        // Single button: show only primary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = if (centerButtons) Arrangement.Center else Arrangement.End
                        ) {
                            Button(
                                onClick = primaryButton.onClick,
                                enabled = primaryButton.enabled,
                                modifier = Modifier
                                    .width(primaryButton.width)
                                    .height(primaryButton.height),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (primaryButton.isPrimary) AppColors.PrimaryColor else Color.Gray
                                ),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = primaryButton.text,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
