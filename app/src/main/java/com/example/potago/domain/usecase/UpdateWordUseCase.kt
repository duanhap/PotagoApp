package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.repository.FlashcardRepository
import javax.inject.Inject

class UpdateWordUseCase @Inject constructor(
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(
        wordId: Long,
        term: String,
        definition: String,
        description: String,
        status: String
    ): Result<Word> {
        return repository.updateWord(wordId, term, definition, description, status)
    }
}
