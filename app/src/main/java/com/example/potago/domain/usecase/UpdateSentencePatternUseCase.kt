package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.SetencePattern
import com.example.potago.domain.repository.SentencePatternRepository
import javax.inject.Inject

class UpdateSentencePatternUseCase @Inject constructor(
    private val sentencePatternRepository: SentencePatternRepository
) {
    suspend operator fun invoke(
        id: Int,
        name: String,
        description: String,
        termLangCode: String,
        defLangCode: String,
        isPublic: Boolean
    ): Result<SetencePattern> {
        return sentencePatternRepository.updateSentencePattern(
            id = id,
            name = name,
            description = description,
            termLangCode = termLangCode,
            defLangCode = defLangCode,
            isPublic = isPublic
        )
    }
}
