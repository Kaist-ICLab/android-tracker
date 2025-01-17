package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kaist.iclab.field_tracker.ui.theme.Gray50
import kaist.iclab.field_tracker.ui.theme.Gray500

@Composable
fun SettingRow(
    title: String,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    showDivider: Boolean = false,
    tail: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (tail != null) Arrangement.SpaceBetween else Arrangement.Start
    ) {
        Column(
            verticalArrangement = if (subtitle != null) Arrangement.spacedBy(2.dp) else Arrangement.Center
        ) {
            Text(
                text = title,
                fontSize = 12.sp
            )
            subtitle?.let {
                Text(
                    text = it,
                    fontSize = 9.sp,
                    color = Gray500
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showDivider) {
                VerticalDivider(
                    modifier = Modifier
                        .height(14.dp),
                    thickness = 1.dp,
                    color = Gray50
                )
            }
            tail?.let {
                it()
            }
        }
    }
}

@Composable
fun SettingSwitchRow(
    title: String,
    subtitle: String? = null,
    switchStatus: SwitchStatus,
    onClick: (() -> Unit)? = null,
) {
    SettingRow(title, subtitle, onClick) {
        CustomSwitch(switchStatus)
    }
}

@Composable
fun SettingEditRow(
    title: String,
    subtitle: String? = null,
    onButtonClick: () -> Unit
) {
    SettingRow(title, subtitle, showDivider = true) {
        IconButton(
            modifier = Modifier.size(48.dp),
            onClick = onButtonClick
        ) {
            Icon(
                Icons.Filled.Tune,
                contentDescription = "Edit",
                tint = Gray500,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SettingNextRow(
    title: String,
    subtitle: String? = null,
    onClick: () -> Unit
) {
    SettingRow(title, subtitle, showDivider = true, onClick = onClick) {
        IconButton(
            modifier = Modifier.size(48.dp),
            onClick = onClick
        ) {
            Icon(
                Icons.Filled.ChevronRight,
                contentDescription = "Edit",
                tint = Gray500,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun SettingRowPreview() {
    val switchStatus = SwitchStatus(
        isChecked = true,
        onCheckedChange = { },
        disabled = false
    )
    Column {
        SettingSwitchRow("Location", "Ready", switchStatus)
        SettingEditRow("Location", "Psick", {})
        SettingNextRow("Location", "Psick", {})
    }
}