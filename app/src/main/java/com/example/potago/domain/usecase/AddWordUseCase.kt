package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class AddWordUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(
        wordSetId: Long,
        term: String,
        definition: String,
        description: String?
    ): Result<Word> {
        return repository.addWord(
            wordSetId = wordSetId,
            term = term,
            definition = definition,
            description = description
        )
    }
}
