package kaist.iclab.field_tracker.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kaist.iclab.field_tracker.ui.components.BaseRow
import kaist.iclab.field_tracker.ui.components.Header
import kaist.iclab.field_tracker.ui.components.ListCard
import kaist.iclab.field_tracker.ui.theme.MainTheme
import kaist.iclab.tracker.auth.User

@Composable
fun UserProfileScreen(
    user: User,
    logout: () -> Unit,
    canNavigateBack: Boolean,
    navigateBack: (() -> Unit)
) {
    Scaffold(
        topBar = {
            Header(
                title = "User Profile",
                canNavigateBack = canNavigateBack,
                navigateBack = navigateBack
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            ListCard(
                rows = listOf(
                    { BaseRow("Name", subtitle = user.name) },
//                    { BaseRow("Gender", subtitle = user.gender) },
//                    {
//                        BaseRow(
//                            "Birth date / Age",
//                            subtitle = "${user.birthDate} / ${user.age}"
//                        )
//                    },
                    {
                        /*TODO: Logout should show modal*/
                        BaseRow("Logout", onClick = logout) {
                            IconButton(
                                modifier = Modifier.size(48.dp),
                                onClick = logout
                            ) {
                                Icon(
                                    Icons.AutoMirrored.Filled.Logout,
                                    tint = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.size(24.dp),
                                    contentDescription = "Logout"
                                )
                            }
                        }
                    },
                )
            )

        }
    }
}

@Preview(showBackground = true)
@Composable
fun UserProfileScreenPreview() {
    MainTheme {
        UserProfileScreen(
            user = User(
                name = "John Doe",
//                gender = "Male",
                email = "john.doe@example",
//                birthDate = "1990-01-01",
//                age = 31,
            ), {},
            canNavigateBack = true,
            navigateBack = {}
        )
    }

}
