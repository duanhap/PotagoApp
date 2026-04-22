package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.SentenceRepository
import javax.inject.Inject

class SaveSubtitleToPatternUseCase @Inject constructor(
    private val repository: SentenceRepository
) {
    suspend operator fun invoke(
        patternId: Int,
        term: String,
        definition: String
    ): Result<Setence> {
        return repository.createSentence(patternId, term, definition)
    }
}
