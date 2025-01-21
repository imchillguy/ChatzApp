package com.chillguy.chatzapp.store

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(DataStoreManager.CHATZ_DATA_STORE_PREFERENCE)

class DataStoreManager(val context: Context) {

    companion object {
        const val CHATZ_DATA_STORE_PREFERENCE = "chatz_data_store_preference"
        private const val LOGGED_IN_USER_ID = "logged_in_user_id"
        val LOGGED_IN_USER_ID_PREF_KEY = stringPreferencesKey(LOGGED_IN_USER_ID)
    }

    suspend fun <T> writeData(key: Preferences.Key<T>, value: T){
        context.dataStore.edit { preference ->
            preference[key] = value
        }
    }

    suspend fun <T> readData(key: Preferences.Key<T>, defaultValue: T?): T? {
        val preference = context.dataStore.data.first()
        return preference[key] ?: defaultValue
    }

}