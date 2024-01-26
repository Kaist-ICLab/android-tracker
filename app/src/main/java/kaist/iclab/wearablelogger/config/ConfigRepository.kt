package kaist.iclab.wearablelogger.config

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kaist.iclab.wearablelogger.ui.SensorState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class ConfigRepository(private val androidContext: Context) {
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore("CONFIG")
    val sensorStatusFlow: Flow<List<SensorState>> = androidContext.dataStore.data
        .map { preferences ->
            listOf(
                SensorState("Skin Temperature",
                    preferences[booleanPreferencesKey("Skin Temperature")] ?: false),
                SensorState("PPG Green",
                    preferences[booleanPreferencesKey("PPG Green")] ?: false),
                SensorState("Heart Rate",
                    preferences[booleanPreferencesKey("Heart Rate")] ?: false),
                SensorState("Accelerometer",
                    preferences[booleanPreferencesKey("Accelerometer")] ?: false),
            )
        }
    val isCollectingFlow: Flow<Boolean> = androidContext.dataStore.data
        .map{ preferences ->
            preferences[booleanPreferencesKey("isCollecting")] ?: false
        }
    suspend fun updateSensorStatus(sensorName: String, status: Boolean) {
        androidContext.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(sensorName)] = status
        }
    }
    suspend fun updateCollectorStatus(status: Boolean){
        androidContext.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey("isCollecting")] = status
        }
    }

    suspend fun getSensorStatus(sensorName: String):Boolean{
        val preferences = androidContext.dataStore.data.first()
        return preferences[booleanPreferencesKey(sensorName)] ?: false
    }
}