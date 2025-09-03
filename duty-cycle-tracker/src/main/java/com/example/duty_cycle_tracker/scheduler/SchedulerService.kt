package com.example.duty_cycle_tracker.scheduler

import android.app.Service
import android.content.Intent
import android.os.IBinder
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SchedulerService: Service() {
    private var job: Job? = null
    val controller by inject<BackgroundController>()

    override fun onCreate() {
        super.onCreate()
        CoroutineScope(Dispatchers.IO).launch {
            // TODO: subscribe to the 3 factors
        }
    }

    override fun onBind(p0: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // TODO: foregroundService

        doSchedule()
        return super.onStartCommand(intent, flags, startId)
    }

    private fun doSchedule() {
        job = CoroutineScope(Dispatchers.IO).launch {
            while(isActive) {
                // TODO: switch isInOnPeriod
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }
}