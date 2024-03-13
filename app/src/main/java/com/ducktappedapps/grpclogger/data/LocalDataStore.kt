package com.ducktappedapps.grpclogger.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest

interface LocalDataStore {
    fun logsEnabled(): Flow<Boolean>

    suspend fun toggleLogging()
}

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class LocalDataStoreImpl(private val context: Context) : LocalDataStore {
    private val LOGGING_ENABLED = booleanPreferencesKey("logging_enabled")
    override fun logsEnabled(): Flow<Boolean> {
        return context
            .dataStore
            .data
            .transformLatest { preferences ->
                val currentValue = preferences[LOGGING_ENABLED]
                if (currentValue == null) {
                    toggleLogging()
                } else {
                    emit(currentValue)
                }
            }
    }

    override suspend fun toggleLogging() {
        context.dataStore.edit {
            val isLoggingEnabled = it[LOGGING_ENABLED] ?: false
            it[LOGGING_ENABLED] = !isLoggingEnabled
        }
    }
}

