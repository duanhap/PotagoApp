package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence

interface SentenceRepository {
    suspend fun getRecentSentences(limit: Int): Result<List<Setence>>
}
