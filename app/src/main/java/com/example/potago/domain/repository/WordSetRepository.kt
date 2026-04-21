package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.WordSet

interface WordSetRepository {
    suspend fun getWordSets(): Result<List<WordSet>>
    suspend fun getRecentWordSets(limit: Int = 3): Result<List<WordSet>>
    suspend fun getWordSetById(wordSetId: Long): Result<WordSet>
    suspend fun createWordSetWithWords(
        name: String,
        description: String?,
        termLangCode: String,
        defLangCode: String,
        words: List<Pair<String, String>>  // term to definition
    ): Result<WordSet>
    suspend fun updateWordSet(
        wordSetId: Long,
        defLangCode: String?,
        description: String?,
        isPublic: Boolean,
        name: String?,
        termLangCode: String?
    ): Result<WordSet>
    suspend fun getWordsByWordSetId(wordSetId: Long, status: String? = null): Result<List<Word>>
}
