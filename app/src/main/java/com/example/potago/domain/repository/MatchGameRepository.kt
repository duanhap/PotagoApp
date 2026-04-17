package com.example.potago.domain.repository

import com.example.potago.data.remote.dto.BestTimeDto
import com.example.potago.data.remote.dto.MatchCardDto
import com.example.potago.data.remote.dto.SubmitMatchGameResponseData
import com.example.potago.domain.model.Result

interface MatchGameRepository {
    suspend fun startGame(wordSetId: Long): Result<Pair<Int, List<MatchCardDto>>>
    suspend fun submitResult(gameId: Int, wordSetId: Long, completedTime: Double): Result<SubmitMatchGameResponseData>
    suspend fun getBestTime(wordSetId: Long): Result<BestTimeDto?>
}
