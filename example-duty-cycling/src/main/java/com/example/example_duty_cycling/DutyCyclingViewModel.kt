package com.example.example_duty_cycling

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DutyCyclingViewModel : ViewModel(), KoinComponent {
    
    private val dutyCyclingManager: SimpleDutyCyclingManager by inject()
    
    val dutyStateFlow: StateFlow<SimpleDutyCyclingManager.DutyState> = dutyCyclingManager.dutyStateFlow
    val lastStateChangeFlow: StateFlow<Long> = dutyCyclingManager.lastStateChangeFlow
    
    init {
        viewModelScope.launch {
            // Start the duty cycling manager when ViewModel is created
            dutyCyclingManager.start()
        }
    }
    
    override fun onCleared() {
        super.onCleared()
        dutyCyclingManager.stop()
    }
    
    fun getCurrentState(): SimpleDutyCyclingManager.DutyState = dutyCyclingManager.getCurrentState()
    
    fun getLastStateChange(): Long = dutyCyclingManager.getLastStateChange()
}
