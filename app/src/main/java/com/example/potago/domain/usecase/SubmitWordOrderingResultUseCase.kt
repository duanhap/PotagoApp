package com.example.potago.domain.usecase

import com.example.potago.data.remote.dto.SubmitWordOrderingResponse
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.WordOrderingRepository
import javax.inject.Inject

class SubmitWordOrderingResultUseCase @Inject constructor(
    private val repository: WordOrderingRepository
) {
    suspend operator fun invoke(
        gameId: Int,
        patternId: Int,
        correctSentenceIds: List<Int>,
        wrongSentenceIds: List<Int>,
        hackExperience: Boolean = false,
        superExperience: Boolean = false
    ): Result<SubmitWordOrderingResponse> {
        return repository.submitResult(
            gameId, patternId, correctSentenceIds, wrongSentenceIds,
            hackExperience, superExperience
        )
    }
}
