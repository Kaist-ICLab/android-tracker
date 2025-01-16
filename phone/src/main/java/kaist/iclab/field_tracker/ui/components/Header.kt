package kaist.iclab.field_tracker.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Header() {
    TopAppBar(
        title = {
            Text(text = "Settings",
                fontWeight = FontWeight.SemiBold)
        },
//        actions = {
//            IconButton(onClick = { /* Handle menu click */ }) {
//                Icon(
//                    imageVector = Icons.Default.Menu,  // This is the standard menu icon
//                    contentDescription = "Menu"
//                )
//            }
//        },
        colors = TopAppBarColors(
            containerColor = Color.White,
            titleContentColor = Color.Black,
            actionIconContentColor = Color.Black,
            scrolledContainerColor = Color.White,
            navigationIconContentColor = Color.Black
        )
    )
}


@Preview(showBackground = true)
@Composable
fun HeaderPreview() {
    Header()
}