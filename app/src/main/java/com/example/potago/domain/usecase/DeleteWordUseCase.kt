package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class DeleteWordUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(wordId: Long): Result<Unit> {
        return repository.deleteWord(wordId)
    }
}
