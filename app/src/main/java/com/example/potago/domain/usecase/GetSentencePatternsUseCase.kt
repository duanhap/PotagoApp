package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.repository.SentencePatternRepository
import javax.inject.Inject

class GetSentencePatternsUseCase @Inject constructor(
    private val sentencePatternRepository: SentencePatternRepository
) {
    suspend operator fun invoke(): Result<List<SetencePattern>> {
        return sentencePatternRepository.getSentencePatterns()
    }
}

