package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import kaist.iclab.field_tracker.ui.theme.Blue500
import kaist.iclab.field_tracker.ui.theme.Gray100
import kaist.iclab.field_tracker.ui.theme.Gray300
import kaist.iclab.field_tracker.ui.theme.Gray500

@Composable
fun SimpleAlertDialog(
    showDialog: Boolean,
    title: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    child: @Composable () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = { onDismiss() }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .background(Color.White, shape = RoundedCornerShape(4.dp))
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp),
                ) {

                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                    )
                    child()

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(color = Blue500)
                                .clickable { onConfirm() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Confirm", color = Color.White, fontWeight = FontWeight.Medium)
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .background(color = Color.White)
                                .clip(RoundedCornerShape(4.dp))
                                .border(1.dp, Gray300, shape = RoundedCornerShape(4.dp))
                                .clickable { onDismiss() },
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Cancel", fontWeight = FontWeight.Medium)
                        }

                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SimpleAlertDialogPreview() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        SimpleAlertDialog(
            showDialog = true,
            title = "Dialog Title",
            onDismiss = {},
            onConfirm = {}) {
            Text("This is a dialog message", color = Gray500, fontSize = 16.sp)
        }
    }
}
