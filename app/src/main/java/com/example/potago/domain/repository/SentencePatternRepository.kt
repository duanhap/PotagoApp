package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.SetencePattern

interface SentencePatternRepository {
    suspend fun getSentencePatterns(): Result<List<SetencePattern>>
    suspend fun getRecentSentencePatterns(): Result<List<SetencePattern>>
    suspend fun createSentencePatternWithSentences(
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        sentences: List<Pair<String, String>>
    ): Result<SetencePattern>
}

