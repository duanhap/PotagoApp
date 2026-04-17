package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.model.reponse.FlashcardsResponse
import com.example.potago.domain.repository.FlashcardRepository
import javax.inject.Inject

class GetFlashcardsUseCase (
    private val repository: FlashcardRepository
) {
    suspend operator fun invoke(
        wordSetId: Long,
        mode: String,
        currentWordId: Long? = null,
        size: Int = 20,
        filter: String = "all"
    ): Result<FlashcardsResponse> {
        return repository.getFlashcards(wordSetId, mode, currentWordId, size, filter)
    }
}
