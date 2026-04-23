package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Start game ────────────────────────────────────────────────────────────────

data class StartWordOrderingRequest(
    @SerializedName("pattern_id") val patternId: Int
)

data class StartWordOrderingResponse(
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("sentences") val sentences: List<SentenceDto>,
    @SerializedName("total") val total: Int
)

// ── Submit result ─────────────────────────────────────────────────────────────

data class SubmitWordOrderingRequest(
    @SerializedName("game_id") val gameId: Int,
    @SerializedName("pattern_id") val patternId: Int,
    @SerializedName("correct_sentence_ids") val correctSentenceIds: List<Int>,
    @SerializedName("wrong_sentence_ids") val wrongSentenceIds: List<Int>,
    @SerializedName("hackExperience") val hackExperience: Boolean = false,
    @SerializedName("superExperience") val superExperience: Boolean = false
)

data class SubmitWordOrderingResponse(
    @SerializedName("correct_count") val correctCount: Int,
    @SerializedName("total_count") val totalCount: Int,
    @SerializedName("experience_earned") val experienceEarned: Int,
    @SerializedName("diamond_earned") val diamondEarned: Int,
    @SerializedName("new_experience") val newExperience: Int,
    @SerializedName("new_diamond") val newDiamond: Int,
    @SerializedName("streak") val streak: RewardStreakDto
)
