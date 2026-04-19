package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class UpdateWordSetUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(
        wordSetId: Long,
        defLangCode: String?,
        description: String?,
        isPublic: Boolean,
        name: String?,
        termLangCode: String?
    ): Result<WordSet> {
        return repository.updateWordSet(
            wordSetId = wordSetId,
            defLangCode = defLangCode,
            description = description,
            isPublic = isPublic,
            name = name,
            termLangCode = termLangCode
        )
    }
}
