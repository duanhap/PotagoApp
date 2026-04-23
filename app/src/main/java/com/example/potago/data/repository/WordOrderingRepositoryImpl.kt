package com.example.potago.data.repository

import com.example.potago.data.remote.api.WordOrderingApiService
import com.example.potago.data.remote.dto.StartWordOrderingRequest
import com.example.potago.data.remote.dto.SubmitWordOrderingRequest
import com.example.potago.data.remote.dto.SubmitWordOrderingResponse
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Setence
import com.example.potago.domain.repository.WordOrderingRepository
import javax.inject.Inject

class WordOrderingRepositoryImpl @Inject constructor(
    private val api: WordOrderingApiService
) : WordOrderingRepository {

    override suspend fun startGame(patternId: Int): Result<Pair<Int, List<Setence>>> {
        return try {
            val response = api.startGame(StartWordOrderingRequest(patternId))
            if (response.success && response.data != null) {
                val sentences = response.data.sentences.map { it.toDomain() }
                Result.Success(Pair(response.data.gameId, sentences))
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun submitResult(
        gameId: Int,
        patternId: Int,
        correctSentenceIds: List<Int>,
        wrongSentenceIds: List<Int>,
        hackExperience: Boolean,
        superExperience: Boolean
    ): Result<SubmitWordOrderingResponse> {
        return try {
            val response = api.submitResult(
                SubmitWordOrderingRequest(
                    gameId = gameId,
                    patternId = patternId,
                    correctSentenceIds = correctSentenceIds,
                    wrongSentenceIds = wrongSentenceIds,
                    hackExperience = hackExperience,
                    superExperience = superExperience
                )
            )
            if (response.success && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}
