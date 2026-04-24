package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.SentenceRepository
import javax.inject.Inject

class UpdateSentenceUseCase @Inject constructor(
    private val sentenceRepository: SentenceRepository
) {
    suspend operator fun invoke(
        id: Int,
        term: String,
        definition: String,
        status: String = "unknown",
        mistakes: Int = 0
    ): Result<Setence> {
        return sentenceRepository.updateSentence(
            id = id,
            term = term,
            definition = definition,
            status = status,
            mistakes = mistakes
        )
    }
}
