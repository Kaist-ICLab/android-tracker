package dev.iclab.tracker.ui

data class MainUiState(
    val isRunning: Boolean = false,
    val data: List<String> = listOf()
)
