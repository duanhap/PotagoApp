package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class GetWordByIdUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(wordId: Long): Result<Word> {
        return repository.getWordById(wordId)
    }
}
