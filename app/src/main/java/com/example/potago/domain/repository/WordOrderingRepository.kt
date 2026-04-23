package com.example.potago.domain.repository

import com.example.potago.data.remote.dto.SubmitWordOrderingResponse
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence

interface WordOrderingRepository {
    suspend fun startGame(patternId: Int): Result<Pair<Int, List<Setence>>>
    suspend fun submitResult(
        gameId: Int,
        patternId: Int,
        correctSentenceIds: List<Int>,
        wrongSentenceIds: List<Int>,
        hackExperience: Boolean = false,
        superExperience: Boolean = false
    ): Result<SubmitWordOrderingResponse>
}
