package kaist.iclab.tracker.sensor.phone

import android.content.Context
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import kaist.iclab.tracker.permission.PermissionManager
import kaist.iclab.tracker.sensor.core.BaseSensor
import kaist.iclab.tracker.sensor.core.SensorConfig
import kaist.iclab.tracker.sensor.core.SensorEntity
import kaist.iclab.tracker.sensor.core.SensorState
import kaist.iclab.tracker.storage.core.StateStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

class MediaSensor(
    private val context: Context,
    permissionManager: PermissionManager,
    configStorage: StateStorage<Config>,
    private val stateStorage: StateStorage<SensorState>,
) : BaseSensor<MediaSensor.Config, MediaSensor.Entity>(
    permissionManager, configStorage, stateStorage, Config::class, Entity::class
) {
    
    class Config : SensorConfig {
        val monitorImages: Boolean = true
        val monitorVideos: Boolean = true
        val monitorAudio: Boolean = true
        val monitorDocuments: Boolean = false
        val debounceDelayMillis: Long = 1000L // Delay to avoid multiple rapid events
    }

    @Serializable
    data class Entity(
        val received: Long,
        val timestamp: Long,
        val operation: String, // "CREATE", "UPDATE", "DELETE"
        val mediaType: String, // "IMAGE", "VIDEO", "AUDIO", "DOCUMENT"
        val uri: String,
        val fileName: String?,
        val mimeType: String?,
        val size: Long?,
        val dateAdded: Long?,
        val dateModified: Long?
    ) : SensorEntity()

    override val permissions: Array<String>
        get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses granular media permissions
            arrayOf(
                android.Manifest.permission.READ_MEDIA_IMAGES,
                android.Manifest.permission.READ_MEDIA_VIDEO,
                android.Manifest.permission.READ_MEDIA_AUDIO
            )
        } else {
            // Android 12 and below use READ_EXTERNAL_STORAGE
            arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    
    override val foregroundServiceTypes: Array<Int> = emptyArray()

    private val contentObserver = MediaContentObserver(Handler(Looper.getMainLooper()))
    private var isObserving = false

    init {
        Log.i("MediaSensor", "MediaSensor initialized")
        Log.i("MediaSensor", "Required permissions: ${permissions.joinToString()}")
        Log.i("MediaSensor", "Android version: ${Build.VERSION.SDK_INT}")
        
        // Monitor state changes
        CoroutineScope(Dispatchers.Main).launch {
            sensorStateFlow.collect { state ->
                Log.i("MediaSensor", "🔄 State changed: $state")
                when (state.flag) {
                    kaist.iclab.tracker.sensor.core.SensorState.FLAG.RUNNING -> {
                        Log.i("MediaSensor", "🚀 MediaSensor is now RUNNING!")
                    }
                    kaist.iclab.tracker.sensor.core.SensorState.FLAG.ENABLED -> {
                        Log.i("MediaSensor", "✅ MediaSensor is ENABLED but not RUNNING")
                    }
                    kaist.iclab.tracker.sensor.core.SensorState.FLAG.DISABLED -> {
                        Log.i("MediaSensor", "❌ MediaSensor is DISABLED")
                    }
                    kaist.iclab.tracker.sensor.core.SensorState.FLAG.UNAVAILABLE -> {
                        Log.i("MediaSensor", "⚠️ MediaSensor is UNAVAILABLE")
                    }
                }
            }
        }
    }

    private inner class MediaContentObserver(handler: Handler) : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            Log.i("MediaSensor", "Media change detected: $uri")
            handleMediaChange(uri)
        }
    }

    private fun handleMediaChange(uri: Uri?) {
        if (uri == null) return
        
        val config = configStateFlow.value
        val timestamp = System.currentTimeMillis()
        
        try {
            // Determine the operation and media type based on the URI
            val (operation, mediaType) = determineOperationAndType(uri)
            
            if (!shouldMonitorMediaType(mediaType, config)) {
                return
            }
            
            // Get media file details
            val mediaInfo = getMediaInfo(uri, mediaType)
            
            listeners.forEach { listener ->
                listener.invoke(
                    Entity(
                        received = timestamp,
                        timestamp = timestamp,
                        operation = operation,
                        mediaType = mediaType,
                        uri = uri.toString(),
                        fileName = mediaInfo.fileName,
                        mimeType = mediaInfo.mimeType,
                        size = mediaInfo.size,
                        dateAdded = mediaInfo.dateAdded,
                        dateModified = mediaInfo.dateModified
                    )
                )
            }
            
            Log.i("MediaSensor", "Media event: $operation $mediaType - ${mediaInfo.fileName}")
            
        } catch (e: Exception) {
            Log.e("MediaSensor", "Error handling media change", e)
        }
    }

    private fun determineOperationAndType(uri: Uri): Pair<String, String> {
        val uriString = uri.toString()
        
        // Determine media type based on URI path
        val mediaType = when {
            uriString.contains(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString()) -> "IMAGE"
            uriString.contains(MediaStore.Video.Media.EXTERNAL_CONTENT_URI.toString()) -> "VIDEO"
            uriString.contains(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI.toString()) -> "AUDIO"
            uriString.contains(MediaStore.Files.getContentUri("external").toString()) -> "DOCUMENT"
            else -> "UNKNOWN"
        }
        
        // For now, we'll assume all changes are CREATE/UPDATE operations
        // DELETE operations are harder to detect with ContentObserver
        val operation = "UPDATE" // Could be enhanced to detect CREATE vs UPDATE vs DELETE
        
        return Pair(operation, mediaType)
    }

    private fun shouldMonitorMediaType(mediaType: String, config: Config): Boolean {
        return when (mediaType) {
            "IMAGE" -> config.monitorImages
            "VIDEO" -> config.monitorVideos
            "AUDIO" -> config.monitorAudio
            "DOCUMENT" -> config.monitorDocuments
            else -> false
        }
    }

    private data class MediaInfo(
        val fileName: String?,
        val mimeType: String?,
        val size: Long?,
        val dateAdded: Long?,
        val dateModified: Long?
    )

    private fun getMediaInfo(uri: Uri, mediaType: String): MediaInfo {
        val projection = arrayOf(
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.MIME_TYPE,
            MediaStore.MediaColumns.SIZE,
            MediaStore.MediaColumns.DATE_ADDED,
            MediaStore.MediaColumns.DATE_MODIFIED
        )
        
        return try {
            context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    MediaInfo(
                        fileName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DISPLAY_NAME)),
                        mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.MIME_TYPE)),
                        size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.SIZE)),
                        dateAdded = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_ADDED)) * 1000,
                        dateModified = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATE_MODIFIED)) * 1000
                    )
                } else {
                    MediaInfo(null, null, null, null, null)
                }
            } ?: MediaInfo(null, null, null, null, null)
        } catch (e: Exception) {
            Log.e("MediaSensor", "Error getting media info for $uri", e)
            MediaInfo(null, null, null, null, null)
        }
    }

    private fun hasMediaPermissions(): Boolean {
        val hasPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ uses granular media permissions
            val hasImages = context.checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED
            val hasVideos = context.checkSelfPermission(android.Manifest.permission.READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED
            val hasAudio = context.checkSelfPermission(android.Manifest.permission.READ_MEDIA_AUDIO) == PackageManager.PERMISSION_GRANTED
            
            Log.i("MediaSensor", "Android 13+ permissions - Images: $hasImages, Videos: $hasVideos, Audio: $hasAudio")
            hasImages || hasVideos || hasAudio
        } else {
            // Android 12 and below use READ_EXTERNAL_STORAGE
            val hasStorage = context.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            Log.i("MediaSensor", "Android 12- permissions - Storage: $hasStorage")
            hasStorage
        }
        
        Log.i("MediaSensor", "Has media permissions: $hasPermissions")
        return hasPermissions
    }

    override fun onStart() {
        Log.i("MediaSensor", "Starting media monitoring")
        
        if (isObserving) {
            Log.w("MediaSensor", "Already observing media changes")
            return
        }
        
        if (!hasMediaPermissions()) {
            Log.e("MediaSensor", "Missing media permissions")
            stateStorage.set(SensorState(SensorState.FLAG.UNAVAILABLE, "Missing media permissions"))
            return
        }
        
        val config = configStateFlow.value
        
        try {
            // Register ContentObserver for different media types
            if (config.monitorImages) {
                context.contentResolver.registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    true,
                    contentObserver
                )
                Log.i("MediaSensor", "Registered observer for images")
            }
            
            if (config.monitorVideos) {
                context.contentResolver.registerContentObserver(
                    MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                    true,
                    contentObserver
                )
                Log.i("MediaSensor", "Registered observer for videos")
            }
            
            if (config.monitorAudio) {
                context.contentResolver.registerContentObserver(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    true,
                    contentObserver
                )
                Log.i("MediaSensor", "Registered observer for audio")
            }
            
            if (config.monitorDocuments) {
                context.contentResolver.registerContentObserver(
                    MediaStore.Files.getContentUri("external"),
                    true,
                    contentObserver
                )
                Log.i("MediaSensor", "Registered observer for documents")
            }
            
            isObserving = true
            Log.i("MediaSensor", "Media monitoring started successfully")
            
        } catch (e: Exception) {
            Log.e("MediaSensor", "Error starting media monitoring", e)
            stateStorage.set(SensorState(SensorState.FLAG.UNAVAILABLE, "Failed to start media monitoring: ${e.message}"))
        }
    }

    override fun onStop() {
        Log.i("MediaSensor", "Stopping media monitoring")
        
        if (!isObserving) {
            Log.w("MediaSensor", "Not currently observing media changes")
            return
        }
        
        try {
            context.contentResolver.unregisterContentObserver(contentObserver)
            isObserving = false
            Log.i("MediaSensor", "Media monitoring stopped successfully")
            
        } catch (e: Exception) {
            Log.e("MediaSensor", "Error stopping media monitoring", e)
        }
    }
}
