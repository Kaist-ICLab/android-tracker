package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.theme.MainTheme

@Composable
fun ListCard(
    title: String? = null,
    rows: List<@Composable () -> Unit>
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (title != null) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                text = title.uppercase(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.secondary,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 18.dp)
        ) {
            rows.forEachIndexed { index, rowContent ->
                rowContent()
                if (index < rows.lastIndex) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outline,
                        thickness = 1.dp,
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFF9FAFB)
@Composable
fun ListCardPreview() {
    val switchStatus = SwitchStatus(
        isChecked = true,
        onCheckedChange = { },
        disabled = false
    )
    val rows = listOf<@Composable () -> Unit>(
        {
            BaseRow(
                title = "Activity Recognition",
            ){ BasicSwitch(switchStatus) }
        },{
            BaseRow(
                title = "Location",
            ){ BasicSwitch(switchStatus) }
        }
    )
    MainTheme {
        Column(verticalArrangement = Arrangement.spacedBy(32.dp)) {
            ListCard(title = "Collectors", rows = rows)
            ListCard(rows = rows)
        }
    }
}
