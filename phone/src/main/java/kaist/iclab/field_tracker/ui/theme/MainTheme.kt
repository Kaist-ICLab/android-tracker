package kaist.iclab.field_tracker.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Based on Tailwind CSS color palette
private val Blue500 = Color(0xFF3B82F6)
private val Blue600 = Color(0xFF2563EB)

private val Gray50 = Color(0xFFF9FAFB)
private val Gray100 = Color(0xFFF3F4F6)
private val Gray200 = Color(0xFFE5E7EB)
private val Gray300 = Color(0xFFD1D5DB)
private val Gray500 = Color(0xFF6B7280)
private val Gray600 = Color(0xFF4B5563)

private val LightColorScheme = lightColorScheme(
    primary = Blue500,
    secondary = Blue600, // List Card Label
    background = Gray50, // Background color, divider color
    onBackground = Gray600, // Default color of black text
    surface = Color.White,
    onSurface = Gray500, // Switch off background, Unselected RadioOption
    outline = Gray300 // Divider color,

)

private val MainShapes = Shapes(
    small = RoundedCornerShape(4.dp),   // TextField, Chip 등
    medium = RoundedCornerShape(4.dp), // Card, Dialog 등
    large = RoundedCornerShape(0.dp) // BottomSheet 등
)

private val MainTypography = Typography(
    titleLarge = TextStyle(
        fontSize = 42.sp,
        fontWeight = FontWeight.Medium
    ),
    titleSmall = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    labelLarge = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium
    ),
    labelSmall = TextStyle(
        fontSize = 12.sp,
        fontWeight = FontWeight.Medium
    ),
    labelMedium = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyMedium = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal
    ),
    bodyLarge = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal
    ),
)

@Composable
fun MainTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColorScheme,
        shapes = MainShapes,
        typography = MainTypography
    ) {
        content()
    }
}