package com.example.potago.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.wordSetDataStore by preferencesDataStore(name = "word_set_prefs")

data class WordSetProgress(
    val wordSetId: Long,
    val mode: String = "normal",
    val currentWordId: Long? = null,
    val filter: String = "all"
)

class WordSetDataStore(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val PROGRESS_LIST_KEY = stringPreferencesKey("word_set_progress_list")
    }

    suspend fun saveProgress(progress: WordSetProgress) {
        context.wordSetDataStore.edit { preferences ->
            val currentListJson = preferences[PROGRESS_LIST_KEY]
            val listType = object : TypeToken<MutableList<WordSetProgress>>() {}.type
            val list: MutableList<WordSetProgress> = if (currentListJson != null) {
                try {
                    gson.fromJson(currentListJson, listType)
                } catch (e: Exception) {
                    mutableListOf()
                }
            } else {
                mutableListOf()
            }

            val index = list.indexOfFirst { it.wordSetId == progress.wordSetId }
            if (index != -1) {
                list[index] = progress
            } else {
                list.add(progress)
            }
            preferences[PROGRESS_LIST_KEY] = gson.toJson(list)
        }
    }

    fun getProgress(wordSetId: Long): Flow<WordSetProgress?> {
        return context.wordSetDataStore.data.map { preferences ->
            val currentListJson = preferences[PROGRESS_LIST_KEY]
            if (currentListJson != null) {
                val listType = object : TypeToken<List<WordSetProgress>>() {}.type
                val list: List<WordSetProgress> = try {
                    gson.fromJson(currentListJson, listType)
                } catch (e: Exception) {
                    emptyList()
                }
                list.find { it.wordSetId == wordSetId }
            } else {
                null
            }
        }
    }
}
