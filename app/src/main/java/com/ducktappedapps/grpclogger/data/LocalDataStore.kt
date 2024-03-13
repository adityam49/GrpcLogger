package com.ducktappedapps.grpclogger.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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
            .map { preferences -> preferences[LOGGING_ENABLED] ?: false }
    }
    override suspend fun toggleLogging() {
        context.dataStore.edit {
            val isLoggingEnabled = it[LOGGING_ENABLED] ?: false
            it[LOGGING_ENABLED] = !isLoggingEnabled
        }
    }
}

