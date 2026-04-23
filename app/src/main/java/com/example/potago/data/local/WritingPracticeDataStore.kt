package com.example.potago.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.writingPracticeDataStore: DataStore<Preferences> by preferencesDataStore(name = "writing_practice_prefs")

data class WritingPracticeProgress(
    val patternId: Int,
    val currentIndex: Int,
    val completedSentenceIds: List<Int>, // Danh sách ID câu đã làm đúng
    val incorrectSentenceIds: List<Int>, // Danh sách ID câu sai
    val isRetryRound: Boolean,
    val startTime: Long,
    val lastUpdated: Long = System.currentTimeMillis()
)

class WritingPracticeDataStore(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val PATTERN_ID_KEY = intPreferencesKey("pattern_id")
        private val CURRENT_INDEX_KEY = intPreferencesKey("current_index")
        private val COMPLETED_IDS_KEY = stringPreferencesKey("completed_sentence_ids")
        private val INCORRECT_IDS_KEY = stringPreferencesKey("incorrect_sentence_ids")
        private val IS_RETRY_ROUND_KEY = stringPreferencesKey("is_retry_round")
        private val START_TIME_KEY = longPreferencesKey("start_time")
        private val LAST_UPDATED_KEY = longPreferencesKey("last_updated")
    }

    suspend fun saveProgress(progress: WritingPracticeProgress) {
        context.writingPracticeDataStore.edit { preferences ->
            preferences[PATTERN_ID_KEY] = progress.patternId
            preferences[CURRENT_INDEX_KEY] = progress.currentIndex
            preferences[COMPLETED_IDS_KEY] = gson.toJson(progress.completedSentenceIds)
            preferences[INCORRECT_IDS_KEY] = gson.toJson(progress.incorrectSentenceIds)
            preferences[IS_RETRY_ROUND_KEY] = progress.isRetryRound.toString()
            preferences[START_TIME_KEY] = progress.startTime
            preferences[LAST_UPDATED_KEY] = progress.lastUpdated
        }
    }

    fun getProgress(patternId: Int): Flow<WritingPracticeProgress?> {
        return context.writingPracticeDataStore.data.map { preferences ->
            val savedPatternId = preferences[PATTERN_ID_KEY] ?: return@map null
            
            // Chỉ trả về progress nếu đúng patternId
            if (savedPatternId != patternId) return@map null
            
            val currentIndex = preferences[CURRENT_INDEX_KEY] ?: 0
            val completedIdsJson = preferences[COMPLETED_IDS_KEY] ?: "[]"
            val incorrectIdsJson = preferences[INCORRECT_IDS_KEY] ?: "[]"
            val isRetryRound = preferences[IS_RETRY_ROUND_KEY]?.toBoolean() ?: false
            val startTime = preferences[START_TIME_KEY] ?: System.currentTimeMillis()
            val lastUpdated = preferences[LAST_UPDATED_KEY] ?: System.currentTimeMillis()
            
            val completedIds: List<Int> = gson.fromJson(
                completedIdsJson,
                object : TypeToken<List<Int>>() {}.type
            )
            val incorrectIds: List<Int> = gson.fromJson(
                incorrectIdsJson,
                object : TypeToken<List<Int>>() {}.type
            )
            
            WritingPracticeProgress(
                patternId = savedPatternId,
                currentIndex = currentIndex,
                completedSentenceIds = completedIds,
                incorrectSentenceIds = incorrectIds,
                isRetryRound = isRetryRound,
                startTime = startTime,
                lastUpdated = lastUpdated
            )
        }
    }

    suspend fun clearProgress(patternId: Int) {
        context.writingPracticeDataStore.edit { preferences ->
            val savedPatternId = preferences[PATTERN_ID_KEY]
            if (savedPatternId == patternId) {
                preferences.clear()
            }
        }
    }

    suspend fun clearAllProgress() {
        context.writingPracticeDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}
