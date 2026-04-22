package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.SentenceRepository
import javax.inject.Inject

class CreateSentenceUseCase @Inject constructor(
    private val sentenceRepository: SentenceRepository
) {
    suspend operator fun invoke(
        patternId: Int,
        term: String,
        definition: String,
        status: String = "unknown",
        mistakes: Int = 0
    ): Result<Setence> {
        return sentenceRepository.createSentence(
            patternId = patternId,
            term = term,
            definition = definition,
            status = status,
            mistakes = mistakes
        )
    }
}
