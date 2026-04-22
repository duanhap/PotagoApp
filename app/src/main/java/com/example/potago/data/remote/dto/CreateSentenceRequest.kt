package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

data class CreateSentenceRequest(
    @SerializedName("pattern_id") val patternId: Int,
    @SerializedName("term") val term: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("status") val status: String = "unknown",
    @SerializedName("mistakes") val mistakes: Int = 0
)
