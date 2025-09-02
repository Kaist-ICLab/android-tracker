package com.example.example_duty_cycling

import android.os.Bundle
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set content to the duty cycling screen
        setContentView(R.layout.activity_main)

        // Add the duty cycling fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, DutyCyclingScreen())
                .commit()
        }
    }
}