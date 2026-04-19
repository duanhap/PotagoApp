package com.example.potago.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.potago.domain.model.ActiveItemSession
import com.example.potago.domain.model.Setting
import com.example.potago.domain.model.Streak
import com.example.potago.domain.model.StreakDate
import com.example.potago.domain.model.User
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val USER_KEY = stringPreferencesKey("user_data")
        private val SETTING_KEY = stringPreferencesKey("setting_data")
        private val STREAK_KEY = stringPreferencesKey("streak_data")
        private val TODAY_STREAK_DATE_KEY = stringPreferencesKey("today_streak_date_data")
        private val ACTIVE_ITEM_TYPE_KEY = stringPreferencesKey("active_item_type")
        private val ACTIVE_ITEM_START_TIME_KEY = longPreferencesKey("active_item_start_time")
        private val ACTIVE_ITEM_TOTAL_DURATION_KEY = longPreferencesKey("active_item_total_duration")
    }

    suspend fun saveUser(user: User) {
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = gson.toJson(user)
        }
    }

    fun getUser(): Flow<User?> {
        return context.dataStore.data.map { preferences ->
            val userJson = preferences[USER_KEY]
            if (userJson != null) gson.fromJson(userJson, User::class.java) else null
        }
    }

    suspend fun saveSetting(setting: Setting) {
        context.dataStore.edit { preferences ->
            preferences[SETTING_KEY] = gson.toJson(setting)
        }
    }

    fun getSetting(): Flow<Setting?> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[SETTING_KEY]
            if (json != null) gson.fromJson(json, Setting::class.java) else null
        }
    }

    suspend fun saveStreak(streak: Streak) {
        context.dataStore.edit { preferences ->
            preferences[STREAK_KEY] = gson.toJson(streak)
        }
    }

    fun getStreak(): Flow<Streak?> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[STREAK_KEY]
            if (json != null) gson.fromJson(json, Streak::class.java) else null
        }
    }

    suspend fun saveTodayStreakDate(streakDate: StreakDate) {
        context.dataStore.edit { preferences ->
            preferences[TODAY_STREAK_DATE_KEY] = gson.toJson(streakDate)
        }
    }

    fun getTodayStreakDate(): Flow<StreakDate?> {
        return context.dataStore.data.map { preferences ->
            val json = preferences[TODAY_STREAK_DATE_KEY]
            if (json != null) gson.fromJson(json, StreakDate::class.java) else null
        }
    }

    // --- Active Item Session ---

    suspend fun saveActiveItemSession(session: ActiveItemSession) {
        context.dataStore.edit { preferences ->
            preferences[ACTIVE_ITEM_TYPE_KEY] = session.itemType
            preferences[ACTIVE_ITEM_START_TIME_KEY] = session.startTimeMs
            preferences[ACTIVE_ITEM_TOTAL_DURATION_KEY] = session.totalDurationMs
        }
    }

    suspend fun clearActiveItemSession() {
        context.dataStore.edit { preferences ->
            preferences.remove(ACTIVE_ITEM_TYPE_KEY)
            preferences.remove(ACTIVE_ITEM_START_TIME_KEY)
            preferences.remove(ACTIVE_ITEM_TOTAL_DURATION_KEY)
        }
    }

    fun getActiveItemSession(): Flow<ActiveItemSession?> {
        return context.dataStore.data.map { preferences ->
            val type = preferences[ACTIVE_ITEM_TYPE_KEY] ?: return@map null
            val startTime = preferences[ACTIVE_ITEM_START_TIME_KEY] ?: return@map null
            val duration = preferences[ACTIVE_ITEM_TOTAL_DURATION_KEY] ?: return@map null
            ActiveItemSession(type, startTime, duration)
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { preferences ->
            preferences.remove(USER_KEY)
            preferences.remove(SETTING_KEY)
            preferences.remove(STREAK_KEY)
            preferences.remove(TODAY_STREAK_DATE_KEY)
            preferences.remove(ACTIVE_ITEM_TYPE_KEY)
            preferences.remove(ACTIVE_ITEM_START_TIME_KEY)
            preferences.remove(ACTIVE_ITEM_TOTAL_DURATION_KEY)
        }
    }
}
