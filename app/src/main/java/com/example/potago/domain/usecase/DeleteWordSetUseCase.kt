package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class DeleteWordSetUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(wordSetId: Long): Result<Unit> {
        return repository.deleteWordSet(wordSetId)
    }
}
