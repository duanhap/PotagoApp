package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.SentenceRepository
import javax.inject.Inject

class GetSentenceByIdUseCase @Inject constructor(
    private val sentenceRepository: SentenceRepository
) {
    suspend operator fun invoke(sentenceId: Int): Result<Setence> {
        return sentenceRepository.getSentenceById(sentenceId)
    }
}
