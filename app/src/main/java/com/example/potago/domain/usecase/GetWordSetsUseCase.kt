package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.WordSet
import com.example.potago.domain.repository.WordSetRepository
import javax.inject.Inject

class GetWordSetsUseCase @Inject constructor(
    private val wordSetRepository: WordSetRepository
) {
    suspend operator fun invoke(): Result<List<WordSet>> {
        return wordSetRepository.getWordSets()
    }
}
