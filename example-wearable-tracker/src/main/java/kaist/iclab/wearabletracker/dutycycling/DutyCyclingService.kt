package kaist.iclab.wearabletracker.dutycycling

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import kaist.iclab.tracker.sensor.controller.BackgroundController
import kotlinx.coroutines.*

class DutyCyclingService : Service() {
    
    companion object {
        private const val TAG = "SimpleDutyCyclingService"
        private const val NOTIFICATION_ID = 3001
        private const val CHANNEL_ID = "duty_cycling_channel"
        
        // Actions
        const val ACTION_START_DUTY_CYCLING = "START_DUTY_CYCLING"
        const val ACTION_STOP_DUTY_CYCLING = "STOP_DUTY_CYCLING"
        
        // Extras
        const val EXTRA_SENSING_DURATION = "SENSING_DURATION"
        const val EXTRA_SLEEP_DURATION = "SLEEP_DURATION"
        
        // Static references for service communication
        var sensorController: BackgroundController? = null
        var onSensingStartedCallback: (() -> Unit)? = null
        var onSensingStoppedCallback: (() -> Unit)? = null
    }
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var dutyCyclingJob: Job? = null
    private var isCurrentlySensing = false
    
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Immediately start foreground to avoid timeout
        try {
            startForeground(NOTIFICATION_ID, createNotification("Duty Cycling Service", "Initializing..."))
        } catch (e: SecurityException) {
            Log.w(TAG, "Failed to start foreground service due to notification permission issue", e)
            // Service will still run but without notification
        }
        
        when (intent?.action) {
            ACTION_START_DUTY_CYCLING -> {
                val sensingDuration = intent.getLongExtra(EXTRA_SENSING_DURATION, 60000)
                val sleepDuration = intent.getLongExtra(EXTRA_SLEEP_DURATION, 30000)
                Log.d(TAG, "Received START_DUTY_CYCLING action with sensing=$sensingDuration, sleep=$sleepDuration")
                startDutyCycling(sensingDuration, sleepDuration)
            }
            ACTION_STOP_DUTY_CYCLING -> {
                Log.d(TAG, "Received STOP_DUTY_CYCLING action")
                stopDutyCycling()
            }
        }
        
        return START_STICKY // Restart service if killed
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    private fun startDutyCycling(sensingDurationMs: Long, sleepDurationMs: Long) {
        Log.d(TAG, "Starting duty cycling: sensing=${sensingDurationMs}ms, sleep=${sleepDurationMs}ms")
        Log.d(TAG, "sensorController is null: ${sensorController == null}")
        
        // Stop any existing duty cycling job
        dutyCyclingJob?.cancel()
        dutyCyclingJob = null
        
        if (isCurrentlySensing) {
            Log.d(TAG, "Stopping existing sensing before starting duty cycling")
            stopSensing()
        }
        
        // Update notification
        updateNotification("Duty Cycling Active", "Sensing and sleeping in cycles")
        
        // Start duty cycling coroutine
        dutyCyclingJob = serviceScope.launch {
            Log.d(TAG, "Duty cycling coroutine started")
            while (isActive) {
                try {
                    // Start sensing
                    Log.d(TAG, "=== DUTY CYCLE START ===")
                    Log.d(TAG, "Starting sensing for ${sensingDurationMs}ms")
                    startSensing()
                    
                    // Wait for sensing duration
                    Log.d(TAG, "Waiting for sensing duration: ${sensingDurationMs}ms")
                    delay(sensingDurationMs)
                    
                    // Stop sensing
                    Log.d(TAG, "=== SENSING DURATION COMPLETE ===")
                    Log.d(TAG, "Stopping sensing for ${sleepDurationMs}ms")
                    stopSensing()
                    
                    // Wait for sleep duration
                    Log.d(TAG, "Waiting for sleep duration: ${sleepDurationMs}ms")
                    delay(sleepDurationMs)
                    Log.d(TAG, "=== SLEEP DURATION COMPLETE ===")
                    
                } catch (e: Exception) {
                    Log.e(TAG, "Error in duty cycling", e)
                    Log.e(TAG, "Exception type: ${e.javaClass.simpleName}")
                    Log.e(TAG, "Exception message: ${e.message}")
                    e.printStackTrace()
                    if (e is kotlinx.coroutines.CancellationException) {
                        Log.d(TAG, "Duty cycling was cancelled - this is expected when stopping")
                    } else {
                        Log.e(TAG, "Unexpected error in duty cycling", e)
                    }
                    break
                }
            }
            Log.d(TAG, "Duty cycling coroutine ended")
        }
    }
    
    private fun stopDutyCycling() {
        Log.d(TAG, "Stopping duty cycling")
        
        dutyCyclingJob?.cancel()
        dutyCyclingJob = null
        
        if (isCurrentlySensing) {
            stopSensing()
        }
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            stopForeground(STOP_FOREGROUND_REMOVE)
        } else {
            @Suppress("DEPRECATION")
            stopForeground(true)
        }
        stopSelf()
    }
    
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun startSensing() {
        try {
            Log.d(TAG, "startSensing() called - sensorController: $sensorController")
            sensorController?.start()
            isCurrentlySensing = true
            onSensingStartedCallback?.invoke()
            updateNotification("Sensing Active", "Collecting sensor data")
            Log.d(TAG, "startSensing() completed - isCurrentlySensing: $isCurrentlySensing")
        } catch (e: Exception) {
            Log.e(TAG, "Error starting sensing", e)
        }
    }
    
    private fun stopSensing() {
        try {
            Log.d(TAG, "stopSensing() called - sensorController: $sensorController")
            sensorController?.stop()
            isCurrentlySensing = false
            onSensingStoppedCallback?.invoke()
            updateNotification("Sleeping", "Waiting before next sensing cycle")
            Log.d(TAG, "stopSensing() completed - isCurrentlySensing: $isCurrentlySensing")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping sensing", e)
        }
    }
    
    private fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        } else {
            true // Permission not required for older versions
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Duty Cycling Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps duty cycling running in background"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createNotification(title: String, content: String): Notification {
        val intent = Intent(this, DutyCyclingService::class.java)
        val pendingIntent = try {
            PendingIntent.getService(
                this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        } catch (e: SecurityException) {
            Log.w(TAG, "Failed to create PendingIntent due to permission issue", e)
            null
        }
        
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(content)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setAutoCancel(false)
            .build()
    }
    
    private fun updateNotification(title: String, content: String) {
        if (!hasNotificationPermission()) {
            Log.w(TAG, "Notification permission not granted, skipping notification update")
            return
        }
        
        try {
            val notification = createNotification(title, content)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            Log.w(TAG, "Failed to update notification due to permission issue", e)
        }
    }
    
    override fun onDestroy() {
        super.onDestroy()
        stopDutyCycling()
        serviceScope.cancel()
    }
}
