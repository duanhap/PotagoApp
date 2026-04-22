package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.SetencePattern

interface SentencePatternRepository {
    suspend fun getSentencePatterns(): Result<List<SetencePattern>>
    suspend fun getRecentSentencePatterns(): Result<List<SetencePattern>>
    suspend fun getSentencePatternById(id: Int): Result<SetencePattern>
    suspend fun createSentencePattern(
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        isPublic: Boolean
    ): Result<SetencePattern>
    suspend fun updateSentencePattern(
        id: Int,
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        isPublic: Boolean
    ): Result<SetencePattern>
    suspend fun deleteSentencePattern(id: Int): Result<Unit>
}

