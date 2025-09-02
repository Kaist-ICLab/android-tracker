package com.example.example_duty_cycling

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DutyCyclingScreen : Fragment() {
    
    private val viewModel: DutyCyclingViewModel by activityViewModels()
    
    private lateinit var statusTextView: TextView
    private lateinit var lastChangeTextView: TextView
    private lateinit var logButton: Button
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_duty_cycling, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Initialize views
        statusTextView = view.findViewById(R.id.status_text)
        lastChangeTextView = view.findViewById(R.id.last_change_text)
        logButton = view.findViewById(R.id.log_button)
        
        // Set up log button
        logButton.setOnClickListener {
            showLogs()
        }
        
        // Observe duty state changes
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.dutyStateFlow.collectLatest { state ->
                updateStatusDisplay(state)
            }
        }
        
        // Observe last state change time
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.lastStateChangeFlow.collectLatest { timestamp ->
                updateLastChangeDisplay(timestamp)
            }
        }
    }
    
    private fun updateStatusDisplay(state: SimpleDutyCyclingManager.DutyState) {
        val statusText = when (state) {
            SimpleDutyCyclingManager.DutyState.ACTIVE -> "🟢 ACTIVE - Continuous monitoring running"
            SimpleDutyCyclingManager.DutyState.PAUSED -> "🔴 PAUSED - Monitoring paused"
        }
        statusTextView.text = statusText
    }
    
    private fun updateLastChangeDisplay(timestamp: Long) {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val timeString = dateFormat.format(Date(timestamp))
        lastChangeTextView.text = "Last state change: $timeString"
    }
    
    private fun showLogs() {
        // Show logs in a simple dialog
        val logs = try {
            val logFile = requireContext().filesDir.resolve("duty_cycling.log")
            if (logFile.exists()) {
                logFile.readText()
            } else {
                "No logs available yet"
            }
        } catch (e: Exception) {
            "Error reading logs: ${e.message}"
        }
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Duty Cycling Logs")
            .setMessage(logs)
            .setPositiveButton("OK", null)
            .show()
    }
}
