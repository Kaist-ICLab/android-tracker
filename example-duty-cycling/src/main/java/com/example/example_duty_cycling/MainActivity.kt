package com.example.example_duty_cycling

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.FragmentActivity
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent

class MainActivity : FragmentActivity(), KoinComponent {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    private val dutyCyclingManager: SimpleDutyCyclingManager by inject()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            Log.d(TAG, "MainActivity onCreate started")
            
            // Set content to the duty cycling screen
            setContentView(R.layout.activity_main)
            
            // Add the duty cycling fragment
            if (savedInstanceState == null) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, DutyCyclingScreen())
                    .commit()
            }
            
            // Start duty cycling manager
            dutyCyclingManager.start()
            
            Log.d(TAG, "MainActivity onCreate completed successfully")
            
        } catch (e: Exception) {
            Log.e(TAG, "Error in onCreate", e)
            // Show a simple error message
            setContentView(android.R.layout.simple_list_item_1)
            findViewById<android.widget.TextView>(android.R.id.text1).text = "Error: ${e.message}"
        }
    }
    
    override fun onResume() {
        super.onResume()
        try {
            // App came to foreground
            dutyCyclingManager.onAppOpened()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onResume", e)
        }
    }
    
    override fun onPause() {
        super.onPause()
        try {
            // App went to background
            dutyCyclingManager.onAppMinimized()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onPause", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        try {
            dutyCyclingManager.stop()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onDestroy", e)
        }
    }
}