package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.SentencePatternRepository
import javax.inject.Inject

class DeleteSentencePatternUseCase @Inject constructor(
    private val sentencePatternRepository: SentencePatternRepository
) {
    suspend operator fun invoke(id: Int): Result<Unit> {
        return sentencePatternRepository.deleteSentencePattern(id)
    }
}
