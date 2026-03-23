package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class GetRecentWordSetsUseCase @Inject constructor(
    private val wordSetRepository: WordSetRepository
) {
    suspend operator fun invoke(limit: Int = 3): Result<List<WordSet>> {
        return wordSetRepository.getRecentWordSets(limit)
    }
}
