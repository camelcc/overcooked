package com.camelcc.overcooked

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.pref: DataStore<Preferences> by preferencesDataStore(name = "Login")

object LoginRepository {
    lateinit var pref: DataStore<Preferences>
    private val USER_KEY = intPreferencesKey("login_key")
    lateinit var userIdFlow: Flow<Int>

    fun initialize(context: Context) {
        pref = context.pref
        userIdFlow = pref.data
            .map { pref -> pref[USER_KEY] ?: 0 }
    }

    suspend fun userLogin() {
        pref.edit { pref ->
            pref[USER_KEY] = 123
        }
    }
}