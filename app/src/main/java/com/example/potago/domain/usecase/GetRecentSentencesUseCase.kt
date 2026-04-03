package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.SentenceRepository
import javax.inject.Inject

class GetRecentSentencesUseCase @Inject constructor(
    private val repository: SentenceRepository
) {
    suspend operator fun invoke(limit: Int = 3): Result<List<Setence>> {
        return repository.getRecentSentences(limit)
    }
}
