package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MatchCardDto(
    @SerializedName("card_id") val cardId: String,
    @SerializedName("pair_id") val pairId: Long,
    @SerializedName("content") val content: String,
    @SerializedName("type") val type: String  // "term" | "definition"
)

data class StartMatchGameResponseData(
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("cards") val cards: List<MatchCardDto>,
    @SerializedName("total_pairs") val totalPairs: Int
)

data class StartMatchGameRequest(
    @SerializedName("word_set_id") val wordSetId: Long
)

data class SubmitMatchGameRequest(
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("word_set_id") val wordSetId: Long,
    @SerializedName("completed_time") val completedTime: Double
)

data class BestTimeDto(
    @SerializedName("best_time") val bestTime: Double,
    @SerializedName("date") val date: String
)

data class SubmitMatchGameResponseData(
    @SerializedName("completed_time") val completedTime: Double,
    @SerializedName("best_time") val bestTime: BestTimeDto?
)
