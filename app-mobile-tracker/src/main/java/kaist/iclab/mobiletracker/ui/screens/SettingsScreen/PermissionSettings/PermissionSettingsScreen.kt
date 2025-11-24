package kaist.iclab.mobiletracker.ui.screens.SettingsScreen.PermissionSettings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.tracker.permission.AndroidPermissionManager
import kaist.iclab.tracker.permission.Permission
import kaist.iclab.tracker.permission.PermissionState

/**
 * Permission settings screen
 * Displays all supported permissions with their current state and allows requesting them
 */
@Composable
fun PermissionSettingsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    permissionManager: AndroidPermissionManager
) {
    val context = LocalContext.current
    val permissions = Permission.supportedPermissions.toList()
    
    // Get all permission IDs to register them
    val allPermissionIds = permissions.flatMap { it.ids.toList() }
    val permissionStateMap = permissionManager.getPermissionFlow(allPermissionIds.toTypedArray())
        .collectAsState().value

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with back button and title
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(Styles.HEADER_HEIGHT)
                    .padding(horizontal = Styles.HEADER_HORIZONTAL_PADDING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
                Text(
                    text = context.getString(R.string.menu_permission),
                    fontWeight = FontWeight.Bold,
                    fontSize = Styles.TITLE_FONT_SIZE
                )
            }

            // Description text
            Text(
                text = context.getString(R.string.permission_screen_description),
                color = AppColors.TextPrimary,
                fontSize = Styles.SCREEN_DESCRIPTION_FONT_SIZE,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = Styles.SCREEN_DESCRIPTION_HORIZONTAL_PADDING,
                        end = Styles.SCREEN_DESCRIPTION_HORIZONTAL_PADDING,
                        bottom = Styles.SCREEN_DESCRIPTION_BOTTOM_PADDING
                    )
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = Styles.CARD_CONTAINER_HORIZONTAL_PADDING)
                    .padding(bottom = Styles.SETTING_CONTAINER_BOTTOM_PADDING)
                    .clip(Styles.CONTAINER_SHAPE)
                    .background(AppColors.White)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    itemsIndexed(
                        items = permissions,
                        key = { _, permission -> permission.name }
                    ) { index, permission ->
                        val isLast = index == permissions.size - 1
                        
                        // Aggregate permission state: GRANTED only if all IDs are GRANTED
                        val aggregatedState = aggregatePermissionState(permission, permissionStateMap)
                        
                        PermissionCard(
                            permission = permission,
                            permissionState = aggregatedState,
                            onRequest = {
                                when (aggregatedState) {
                                    PermissionState.GRANTED -> {
                                        // Open settings to allow user to revoke/change permission
                                        openPermissionSettings(context, permission.ids.first())
                                    }
                                    PermissionState.PERMANENTLY_DENIED -> {
                                        // Open settings for permanently denied permissions
                                        openPermissionSettings(context, permission.ids.first())
                                    }
                                    else -> {
                                        // Request permission
                                        permissionManager.request(permission.ids)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Add horizontal divider between cards (not after the last one)
                        if (!isLast) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                HorizontalDivider(
                                    color = AppColors.BorderDark,
                                    thickness = 0.dp,
                                    modifier = Modifier.fillMaxWidth(Styles.DIVIDER_WIDTH_RATIO)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Aggregates permission state for a Permission object with multiple IDs.
 * Returns GRANTED only if all IDs are GRANTED.
 * Otherwise, returns the "worst" state (PERMANENTLY_DENIED > RATIONALE_REQUIRED > NOT_REQUESTED > UNSUPPORTED)
 */
private fun aggregatePermissionState(
    permission: Permission,
    permissionStateMap: Map<String, PermissionState>
): PermissionState {
    val states = permission.ids.map { id ->
        permissionStateMap[id] ?: PermissionState.NOT_REQUESTED
    }
    
    // If all are GRANTED, return GRANTED
    if (states.all { it == PermissionState.GRANTED }) {
        return PermissionState.GRANTED
    }
    
    // If any is UNSUPPORTED, return UNSUPPORTED
    if (states.any { it == PermissionState.UNSUPPORTED }) {
        return PermissionState.UNSUPPORTED
    }
    
    // If any is PERMANENTLY_DENIED, return PERMANENTLY_DENIED
    if (states.any { it == PermissionState.PERMANENTLY_DENIED }) {
        return PermissionState.PERMANENTLY_DENIED
    }
    
    // If any requires rationale, return RATIONALE_REQUIRED
    if (states.any { it == PermissionState.RATIONALE_REQUIRED }) {
        return PermissionState.RATIONALE_REQUIRED
    }
    
    // Otherwise, return NOT_REQUESTED
    return PermissionState.NOT_REQUESTED
}

