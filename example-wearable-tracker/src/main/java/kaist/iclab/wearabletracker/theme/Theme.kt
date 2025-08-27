package kaist.iclab.wearabletracker.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material.MaterialTheme

@Composable
fun WearableTrackerTheme(
    content: @Composable () -> Unit
) {
    /**
     * Empty theme to customize for your app.
     * See: https://kaisteloper.android.com/jetpack/compose/designsystems/custom
     */
    MaterialTheme(
        content = content
    )
}