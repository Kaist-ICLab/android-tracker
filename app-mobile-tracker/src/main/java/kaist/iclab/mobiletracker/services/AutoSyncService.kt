package kaist.iclab.mobiletracker.services

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.IBinder
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.core.component.KoinComponent
import org.koin.core.qualifier.named
import kaist.iclab.mobiletracker.services.upload.PhoneSensorUploadService
import kaist.iclab.tracker.sensor.core.Sensor

/**
 * Service that handles automatic synchronization of sensor data to Supabase
 * based on configured interval and network preferences.
 */
class AutoSyncService : Service(), KoinComponent {
    companion object {
        private const val TAG = "AutoSyncService"
        private const val CHECK_INTERVAL_MS = 60_000L // Check every minute
        
        /**
         * Helper function to start the service from a Context
         */
        fun start(context: Context) {
            val intent = Intent(context, AutoSyncService::class.java)
            context.startService(intent)
        }
        
        /**
         * Helper function to stop the service from a Context
         */
        fun stop(context: Context) {
            val intent = Intent(context, AutoSyncService::class.java)
            context.stopService(intent)
        }
    }

    private val syncTimestampService: SyncTimestampService by lazy {
        SyncTimestampService(this)
    }
    private val phoneSensorUploadService: PhoneSensorUploadService by inject()
    private val sensors by inject<List<Sensor<*, *>>>(qualifier = named("sensors"))

    // Coroutine scope tied to service lifecycle
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var syncJob: Job? = null
    private var lastSyncTime: Long = 0

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "AutoSyncService started")
        startAutoSync()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AutoSyncService destroyed")
        stopAutoSync()
        serviceScope.cancel()
    }

    /**
     * Starts the auto-sync coroutine that periodically checks and syncs data
     */
    private fun startAutoSync() {
        if (syncJob?.isActive == true) {
            Log.d(TAG, "Auto-sync already running")
            return
        }

        syncJob = serviceScope.launch {
            Log.d(TAG, "Auto-sync started")
            lastSyncTime = System.currentTimeMillis()

            while (isActive) {
                try {
                    checkAndSyncIfNeeded()
                    delay(CHECK_INTERVAL_MS)
                } catch (e: Exception) {
                    Log.e(TAG, "Error in auto-sync loop: ${e.message}", e)
                    delay(CHECK_INTERVAL_MS)
                }
            }
        }
    }

    /**
     * Stops the auto-sync coroutine
     */
    private fun stopAutoSync() {
        syncJob?.cancel()
        syncJob = null
        Log.d(TAG, "Auto-sync stopped")
    }

    /**
     * Checks if sync conditions are met and triggers sync if needed
     */
    private suspend fun checkAndSyncIfNeeded() {
        // Check if data collection is running
        val dataCollectionStarted = syncTimestampService.getDataCollectionStarted()
        if (dataCollectionStarted == null) {
            // Data collection not running, don't sync
            return
        }

        // Get auto-sync settings
        val intervalMinutes = syncTimestampService.getAutoSyncIntervalMinutes()
        if (intervalMinutes == SyncTimestampService.AUTO_SYNC_INTERVAL_NONE) {
            // Auto-sync disabled
            return
        }

        // Check if enough time has passed since last sync
        val currentTime = System.currentTimeMillis()
        val timeSinceLastSync = currentTime - lastSyncTime
        val intervalMs = intervalMinutes * 60 * 1000L

        if (timeSinceLastSync < intervalMs) {
            // Not enough time has passed
            return
        }

        // Check network conditions
        if (!isNetworkConditionMet()) {
            // Network condition not met, skip this sync
            Log.d(TAG, "Network condition not met, skipping sync")
            return
        }

        // All conditions met, trigger sync
        Log.d(TAG, "Triggering auto-sync (interval: $intervalMinutes minutes)")
        serviceScope.launch {
            uploadAllSensorData()
        }
        lastSyncTime = currentTime
    }

    /**
     * Checks if the current network connection meets the configured network preference
     */
    private fun isNetworkConditionMet(): Boolean {
        val networkMode = syncTimestampService.getAutoSyncNetworkMode()
        
        // If mode is WIFI_MOBILE, any connection is fine
        if (networkMode == SyncTimestampService.AUTO_SYNC_NETWORK_WIFI_MOBILE) {
            return isConnected()
        }

        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

        val hasWifi = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        val hasCellular = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)

        return when (networkMode) {
            SyncTimestampService.AUTO_SYNC_NETWORK_WIFI_ONLY -> hasWifi
            SyncTimestampService.AUTO_SYNC_NETWORK_MOBILE_ONLY -> hasCellular
            else -> isConnected()
        }
    }

    /**
     * Checks if device has any network connection
     */
    private fun isConnected(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    /**
     * Uploads all sensor data for all phone sensors
     */
    private suspend fun uploadAllSensorData() {
        try {
            sensors.forEach { sensor ->
                if (phoneSensorUploadService.hasDataToUpload(sensor.id, sensor)) {
                    when (val result = phoneSensorUploadService.uploadSensorData(sensor.id, sensor)) {
                        is kaist.iclab.mobiletracker.repository.Result.Success -> {
                            Log.d(TAG, "Auto-sync: Successfully uploaded data for ${sensor.name}")
                            syncTimestampService.updateLastSuccessfulUpload(sensor.id)
                        }
                        is kaist.iclab.mobiletracker.repository.Result.Error -> {
                            Log.e(TAG, "Auto-sync: Error uploading data for ${sensor.name}: ${result.message}", result.exception)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in auto-sync upload: ${e.message}", e)
        }
    }
}
