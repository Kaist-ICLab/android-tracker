package com.example.duty_cycle_tracker.scheduler

import androidx.compose.runtime.mutableStateOf

object DutyStateStorage {
    private val isScreenOn = mutableStateOf(false)
    private val isAppOn = mutableStateOf(false)
    private val isInOnPeriod = mutableStateOf(false)
    private val isTrackingAllowed = mutableStateOf(false)
}