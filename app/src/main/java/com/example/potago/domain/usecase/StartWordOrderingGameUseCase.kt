package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.WordOrderingRepository
import javax.inject.Inject

class StartWordOrderingGameUseCase @Inject constructor(
    private val repository: WordOrderingRepository
) {
    suspend operator fun invoke(patternId: Int): Result<Pair<Int, List<Setence>>> {
        return repository.startGame(patternId)
    }
}
