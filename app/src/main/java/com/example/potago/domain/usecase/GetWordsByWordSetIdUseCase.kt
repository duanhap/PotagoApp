package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Word
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class GetWordsByWordSetIdUseCase @Inject constructor(
    private val repository: WordSetRepository
) {
    suspend operator fun invoke(wordSetId: Long, status: String? = null): Result<List<Word>> {
        return repository.getWordsByWordSetId(wordSetId, status)
    }
}
