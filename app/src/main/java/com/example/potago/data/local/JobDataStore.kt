package com.example.potago.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.potago.domain.model.Video
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class ProcessingJob(
    val video: Video,
    val jobId: String,
    val progress: Int = 0,
    val status: String = "processing"
)

class JobDataStore(private val context: Context) {
    private val gson = Gson()

    companion object {
        private val JOB_KEY = stringPreferencesKey("processing_job")
    }

    suspend fun saveJob(job: ProcessingJob) {
        context.dataStore.edit { preferences ->
            preferences[JOB_KEY] = gson.toJson(job)
        }
    }

    fun getJob(): Flow<ProcessingJob?> {
        return context.dataStore.data.map { preferences ->
            val jobJson = preferences[JOB_KEY]
            if (jobJson != null) {
                gson.fromJson(jobJson, ProcessingJob::class.java)
            } else {
                null
            }
        }
    }

    suspend fun clearJob() {
        context.dataStore.edit { preferences ->
            preferences.remove(JOB_KEY)
        }
    }
}
