package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence

interface SentenceRepository {
    suspend fun getRecentSentences(limit: Int): Result<List<Setence>>
    suspend fun getSentencesByPatternId(
        patternId: Int,
        page: Int? = null,
        pageSize: Int? = null,
        status: String? = null
    ): Result<List<Setence>>
    suspend fun getSentenceById(id: Int): Result<Setence>
    suspend fun createSentenceduan(patternId: Int, term: String, definition: String): Result<Setence>
    suspend fun createSentence(
        patternId: Int,
        term: String,
        definition: String,
        status: String = "unknown",
        mistakes: Int = 0
    ): Result<Setence>
    suspend fun updateSentence(
        id: Int,
        term: String,
        definition: String,
        status: String = "unknown",
        mistakes: Int = 0
    ): Result<Setence>
    suspend fun deleteSentence(id: Int): Result<Unit>
}
