package kaist.iclab.field_tracker.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FilledButton(modifier: Modifier? = null, text: String, onClick: () -> Unit) {
    Button(
        modifier = (modifier ?: Modifier).height(48.dp),
        shape = MaterialTheme.shapes.small,
        onClick = onClick
    ) {
        Text(text, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
fun OutlinedButton(modifier: Modifier? = null, text: String, onClick: () -> Unit) {
    Button(
        modifier = (modifier ?: Modifier).height(48.dp),
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.onBackground,
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(
            color = MaterialTheme.colorScheme.outline,
            width = 1.dp
        ),
        shape = MaterialTheme.shapes.small,
        onClick = onClick
    ) {
        Text(
            text,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.labelMedium
        )
    }
}
