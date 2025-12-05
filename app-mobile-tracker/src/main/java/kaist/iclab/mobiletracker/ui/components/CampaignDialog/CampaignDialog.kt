package kaist.iclab.mobiletracker.ui.components.CampaignDialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import kaist.iclab.mobiletracker.R

/**
 * Campaign selection dialog with radio buttons
 */
@Composable
fun CampaignDialog(
    experiments: List<String>,
    selectedExperiment: String?,
    onDismiss: () -> Unit,
    onSelect: (String) -> Unit
) {
    val context = LocalContext.current
    var selected by remember { mutableStateOf(selectedExperiment) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = context.getString(R.string.campaign_dialog_title),
                fontSize = CampaignDialogStyles.TitleFontSize,
                fontWeight = CampaignDialogStyles.TitleFontWeight,
                color = CampaignDialogStyles.TitleColor
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                experiments.forEach { experiment ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = (selected == experiment),
                                onClick = { selected = experiment },
                                role = Role.RadioButton
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (selected == experiment),
                            onClick = { selected = experiment }
                        )
                        Text(
                            text = experiment,
                            fontSize = CampaignDialogStyles.ExperimentNameFontSize,
                            color = CampaignDialogStyles.ExperimentNameColor
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    selected?.let { onSelect(it) }
                    onDismiss()
                },
                enabled = selected != null,
                modifier = Modifier
                    .width(CampaignDialogStyles.ButtonWidth)
                    .height(CampaignDialogStyles.ButtonHeight),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CampaignDialogStyles.SelectButtonColor
                ),
                shape = CampaignDialogStyles.SelectButtonShape
            ) {
                Text(
                    text = context.getString(R.string.campaign_dialog_select),
                    color = CampaignDialogStyles.SelectButtonTextColor,
                    fontSize = CampaignDialogStyles.SelectButtonTextSize
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                modifier = Modifier
                    .width(CampaignDialogStyles.ButtonWidth)
                    .height(CampaignDialogStyles.ButtonHeight),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CampaignDialogStyles.CancelButtonTextColor
                ),
                border = CampaignDialogStyles.CancelButtonBorder,
                shape = CampaignDialogStyles.CancelButtonShape
            ) {
                Text(
                    text = context.getString(R.string.campaign_dialog_cancel),
                    fontSize = CampaignDialogStyles.CancelButtonTextSize
                )
            }
        },
        containerColor = CampaignDialogStyles.DialogContainerColor,
        shape = CampaignDialogStyles.DialogShape,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    )
}

