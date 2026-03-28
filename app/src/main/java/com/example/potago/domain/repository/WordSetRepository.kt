package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet

interface WordSetRepository {
    suspend fun getWordSets(): Result<List<WordSet>>
    suspend fun getRecentWordSets(limit: Int = 3): Result<List<WordSet>>
    suspend fun deleteWordSet(wordSetId: Long): Result<Unit>
}
