package kaist.iclab.mobiletracker.ui.components.Toggle

import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import kaist.iclab.mobiletracker.ui.theme.AppColors

/**
 * Reusable toggle/switch component with consistent styling across the app
 * 
 * @param checked Whether the toggle is checked/on
 * @param onCheckedChange Callback when the toggle state changes
 * @param modifier Modifier to be applied to the switch
 * @param enabled Whether the toggle is enabled (default: true)
 */
@Composable
fun Toggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = false
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        enabled = enabled,
        modifier = modifier.scale(SWITCH_SCALE),
        colors = switchColors()
    )
}

/**
 * Scale factor for switches to make them smaller
 */
private const val SWITCH_SCALE = 0.7f

/**
 * Global switch colors configuration
 * Used consistently across all switches/toggles in the app
 */
@Composable
private fun switchColors() = SwitchDefaults.colors(
    checkedThumbColor = AppColors.White,
    checkedTrackColor = AppColors.PrimaryColor,
    uncheckedThumbColor = AppColors.White,
    uncheckedTrackColor = AppColors.SwitchOff,
    checkedBorderColor = Color.Transparent,
    uncheckedBorderColor = Color.Transparent,
    disabledCheckedThumbColor = AppColors.White,
    disabledCheckedTrackColor = AppColors.PrimaryColor,
    disabledUncheckedThumbColor = AppColors.White,
    disabledUncheckedTrackColor = AppColors.NavigationBarUnselected
)

