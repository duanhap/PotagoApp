package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class GetWordSetByIdUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(wordSetId: Long): Result<WordSet> {
        return repository.getWordSetById(wordSetId)
    }
}
