package com.example.potago.data.repository

import com.example.potago.data.remote.api.MatchGameApiService
import com.example.potago.data.remote.dto.BestTimeDto
import com.example.potago.data.remote.dto.MatchCardDto
import com.example.potago.data.remote.dto.StartMatchGameRequest
import com.example.potago.data.remote.dto.SubmitMatchGameRequest
import com.example.potago.data.remote.dto.SubmitMatchGameResponseData
import com.example.potago.domain.model.Result
import com.example.potago.domain.repository.MatchGameRepository
import javax.inject.Inject

class MatchGameRepositoryImpl @Inject constructor(
    private val api: MatchGameApiService
) : MatchGameRepository {

    override suspend fun startGame(wordSetId: Long): Result<Pair<Int, List<MatchCardDto>>> {
        return try {
            val response = api.startGame(StartMatchGameRequest(wordSetId))
            if (response.success && response.data != null) {
                Result.Success(Pair(response.data.gameId, response.data.cards))
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun submitResult(
        gameId: Int, wordSetId: Long, completedTime: Double
    ): Result<SubmitMatchGameResponseData> {
        return try {
            val response = api.submitResult(SubmitMatchGameRequest(gameId, wordSetId, completedTime))
            if (response.success && response.data != null) {
                Result.Success(response.data)
            } else {
                Result.Error(response.message ?: "Unknown error")
            }
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }

    override suspend fun getBestTime(wordSetId: Long): Result<BestTimeDto?> {
        return try {
            val response = api.getBestTime(wordSetId)
            Result.Success(response.data)
        } catch (e: Exception) {
            Result.Error("Network error: ${e.message}")
        }
    }
}
