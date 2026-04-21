package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.reponse.FlashcardsResponse

interface FlashcardRepository {
    suspend fun getFlashcards(
        wordSetId: Long,
        mode: String,
        currentWordId: Long? = null,
        size: Int = 20,
        filter: String = "all"
    ): Result<FlashcardsResponse>

    suspend fun updateWordStatus(wordId: Long, status: String): Result<Word>

    suspend fun updateWord(
        wordId: Long,
        term: String,
        definition: String,
        description: String,
        status: String
    ): Result<Word>
}
