package kaist.iclab.mobiletracker.ui.screens.SensorDetailScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import kaist.iclab.mobiletracker.R
import kaist.iclab.mobiletracker.repository.DateFilter
import kaist.iclab.mobiletracker.repository.SensorRecord
import kaist.iclab.mobiletracker.repository.SortOrder
import kaist.iclab.mobiletracker.ui.theme.AppColors
import kaist.iclab.mobiletracker.viewmodels.data.SensorDetailUiState
import kaist.iclab.mobiletracker.viewmodels.data.SensorDetailViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Sensor Detail screen - displays raw data for a specific sensor.
 */
@Composable
fun SensorDetailScreen(
    viewModel: SensorDetailViewModel,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    // Load more when reaching end of list
    LaunchedEffect(listState) {
        snapshotFlow { 
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            lastVisibleItem >= totalItems - 3 && totalItems > 0
        }.collect { shouldLoadMore ->
            if (shouldLoadMore && uiState.hasMoreRecords && !uiState.isLoadingMore) {
                viewModel.loadMoreRecords()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // Header
        SensorDetailHeader(
            title = uiState.sensorInfo?.displayName ?: "",
            isWatchSensor = uiState.sensorInfo?.isWatchSensor == true,
            onNavigateBack = onNavigateBack
        )

        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AppColors.PrimaryColor)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error ?: "",
                        color = AppColors.TextSecondary
                    )
                }
            }
            else -> {
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = Styles.SCREEN_HORIZONTAL_PADDING),
                    verticalArrangement = Arrangement.spacedBy(Styles.ITEM_SPACING)
                ) {
                    // Summary Card
                    item {
                        Spacer(modifier = Modifier.height(Styles.SECTION_SPACING))
                        SummaryCard(uiState = uiState)
                    }
                    
                    // Filter and Sort Row
                    item {
                        FilterSortRow(
                            dateFilter = uiState.dateFilter,
                            sortOrder = uiState.sortOrder,
                            onDateFilterChange = { viewModel.setDateFilter(it) },
                            onSortOrderToggle = { viewModel.toggleSortOrder() }
                        )
                    }
                    
                    // Section header
                    item {
                        Text(
                            text = stringResource(R.string.sensor_detail_raw_data, uiState.filteredCount),
                            fontSize = Styles.SECTION_TITLE_FONT_SIZE,
                            fontWeight = FontWeight.SemiBold,
                            color = AppColors.TextPrimary,
                            modifier = Modifier.padding(vertical = Styles.SECTION_TITLE_VERTICAL_PADDING)
                        )
                    }
                    
                    // Records
                    if (uiState.records.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = Styles.EMPTY_STATE_VERTICAL_PADDING),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.sensor_detail_no_records),
                                    color = AppColors.TextSecondary
                                )
                            }
                        }
                    } else {
                        items(uiState.records, key = { it.id }) { record ->
                            RecordCard(
                                record = record,
                                onDelete = { viewModel.deleteRecord(record.id) }
                            )
                        }
                    }
                    
                    // Loading more indicator
                    if (uiState.isLoadingMore) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Styles.LOADING_INDICATOR_PADDING),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(Styles.LOADING_INDICATOR_SIZE),
                                    color = AppColors.PrimaryColor
                                )
                            }
                        }
                    }
                    
                    // Bottom spacer
                    item {
                        Spacer(modifier = Modifier.height(Styles.BOTTOM_SPACER_HEIGHT))
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorDetailHeader(
    title: String,
    isWatchSensor: Boolean,
    onNavigateBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.White)
            .padding(horizontal = Styles.HEADER_HORIZONTAL_PADDING, vertical = Styles.HEADER_VERTICAL_PADDING),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(R.string.sensor_detail_back),
                tint = AppColors.TextPrimary
            )
        }
        Text(
            text = title,
            fontSize = Styles.HEADER_TITLE_FONT_SIZE,
            fontWeight = FontWeight.SemiBold,
            color = AppColors.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        if (isWatchSensor) {
            Icon(
                imageVector = Icons.Default.Watch,
                contentDescription = "Watch sensor",
                tint = AppColors.TextSecondary,
                modifier = Modifier.size(Styles.WATCH_BADGE_SIZE)
            )
        }
    }
}

@Composable
private fun SummaryCard(uiState: SensorDetailUiState) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        shape = RoundedCornerShape(Styles.CARD_CORNER_RADIUS)
    ) {
        Column(modifier = Modifier.padding(Styles.CARD_PADDING)) {
            Text(
                text = stringResource(R.string.sensor_detail_summary),
                fontSize = Styles.CARD_TITLE_FONT_SIZE,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.TextPrimary
            )
            Spacer(modifier = Modifier.height(Styles.SUMMARY_CONTENT_SPACING))
            
            SummaryRow(
                label = stringResource(R.string.sensor_detail_total_records),
                value = formatNumber(uiState.sensorInfo?.totalRecords ?: 0)
            )
            SummaryRow(
                label = stringResource(R.string.sensor_detail_today),
                value = formatNumber(uiState.sensorInfo?.todayRecords ?: 0)
            )
            SummaryRow(
                label = stringResource(R.string.sensor_detail_last_recorded),
                value = formatDateTime(uiState.sensorInfo?.lastRecordedTime)
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Styles.SUMMARY_ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = Styles.SUMMARY_LABEL_FONT_SIZE,
            color = AppColors.TextSecondary
        )
        Text(
            text = value,
            fontSize = Styles.SUMMARY_VALUE_FONT_SIZE,
            fontWeight = FontWeight.Medium,
            color = AppColors.TextPrimary
        )
    }
}

@Composable
private fun FilterSortRow(
    dateFilter: DateFilter,
    sortOrder: SortOrder,
    onDateFilterChange: (DateFilter) -> Unit,
    onSortOrderToggle: () -> Unit
) {
    var showDateFilterMenu by remember { mutableStateOf(false) }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = Styles.FILTER_ROW_VERTICAL_PADDING),
        horizontalArrangement = Arrangement.spacedBy(Styles.FILTER_BUTTON_SPACING)
    ) {
        // Date Filter Button
        Box {
            Card(
                onClick = { showDateFilterMenu = true },
                colors = CardDefaults.cardColors(containerColor = AppColors.White),
                shape = RoundedCornerShape(Styles.FILTER_BUTTON_CORNER_RADIUS)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = Styles.FILTER_BUTTON_HORIZONTAL_PADDING, vertical = Styles.FILTER_BUTTON_VERTICAL_PADDING),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(Styles.FILTER_ICON_SIZE),
                        tint = AppColors.TextSecondary
                    )
                    Spacer(modifier = Modifier.width(Styles.FILTER_ICON_TEXT_SPACING))
                    Text(
                        text = getDateFilterLabel(dateFilter),
                        fontSize = Styles.FILTER_BUTTON_FONT_SIZE,
                        color = AppColors.TextPrimary
                    )
                }
            }
            
            DropdownMenu(
                expanded = showDateFilterMenu,
                onDismissRequest = { showDateFilterMenu = false }
            ) {
                DateFilter.entries.forEach { filter ->
                    DropdownMenuItem(
                        text = { Text(getDateFilterLabel(filter)) },
                        onClick = {
                            onDateFilterChange(filter)
                            showDateFilterMenu = false
                        }
                    )
                }
            }
        }
        
        // Sort Button
        Card(
            onClick = onSortOrderToggle,
            colors = CardDefaults.cardColors(containerColor = AppColors.White),
            shape = RoundedCornerShape(Styles.FILTER_BUTTON_CORNER_RADIUS)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = Styles.FILTER_BUTTON_HORIZONTAL_PADDING, vertical = Styles.FILTER_BUTTON_VERTICAL_PADDING),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Sort,
                    contentDescription = null,
                    modifier = Modifier.size(Styles.FILTER_ICON_SIZE),
                    tint = AppColors.TextSecondary
                )
                Spacer(modifier = Modifier.width(Styles.FILTER_ICON_TEXT_SPACING))
                Text(
                    text = getSortOrderLabel(sortOrder),
                    fontSize = Styles.FILTER_BUTTON_FONT_SIZE,
                    color = AppColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun RecordCard(
    record: SensorRecord,
    onDelete: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        shape = RoundedCornerShape(Styles.CARD_CORNER_RADIUS)
    ) {
        Column(modifier = Modifier.padding(Styles.RECORD_CARD_PADDING)) {
            // Timestamp header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatDateTime(record.timestamp),
                    fontSize = Styles.RECORD_TIMESTAMP_FONT_SIZE,
                    fontWeight = FontWeight.Medium,
                    color = AppColors.TextPrimary
                )
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.size(Styles.DELETE_BUTTON_SIZE)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.sensor_detail_delete),
                        tint = AppColors.TextSecondary,
                        modifier = Modifier.size(Styles.DELETE_ICON_SIZE)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(Styles.RECORD_CONTENT_SPACING))
            
            // Fields
            record.fields.forEach { (key, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = Styles.FIELD_ROW_VERTICAL_PADDING)
                ) {
                    Text(
                        text = "$key:",
                        fontSize = Styles.FIELD_LABEL_FONT_SIZE,
                        color = AppColors.TextSecondary,
                        modifier = Modifier.width(Styles.FIELD_LABEL_WIDTH)
                    )
                    Text(
                        text = value,
                        fontSize = Styles.FIELD_VALUE_FONT_SIZE,
                        color = AppColors.TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.sensor_detail_delete_title)) },
            text = { Text(stringResource(R.string.sensor_detail_delete_message)) },
            confirmButton = {
                TextButton(onClick = {
                    onDelete()
                    showDeleteDialog = false
                }) {
                    Text(stringResource(R.string.sensor_detail_delete_confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.sensor_detail_delete_cancel))
                }
            }
        )
    }
}

private fun formatNumber(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fK", count / 1_000.0)
        else -> count.toString()
    }
}

private fun formatDateTime(timestamp: Long?): String {
    if (timestamp == null) return "-"
    val dateFormat = SimpleDateFormat("HH:mm:ss · MMM d, yyyy", Locale.getDefault())
    return dateFormat.format(Date(timestamp))
}

@Composable
private fun getDateFilterLabel(filter: DateFilter): String {
    return when (filter) {
        DateFilter.TODAY -> stringResource(R.string.sensor_detail_filter_today)
        DateFilter.LAST_7_DAYS -> stringResource(R.string.sensor_detail_filter_7_days)
        DateFilter.LAST_30_DAYS -> stringResource(R.string.sensor_detail_filter_30_days)
        DateFilter.ALL_TIME -> stringResource(R.string.sensor_detail_filter_all)
    }
}

@Composable
private fun getSortOrderLabel(sortOrder: SortOrder): String {
    return when (sortOrder) {
        SortOrder.NEWEST_FIRST -> stringResource(R.string.sensor_detail_sort_newest)
        SortOrder.OLDEST_FIRST -> stringResource(R.string.sensor_detail_sort_oldest)
    }
}
