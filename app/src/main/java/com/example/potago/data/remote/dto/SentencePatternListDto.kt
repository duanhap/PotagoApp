package com.example.potago.data.remote.dto

import com.google.gson.annotations.SerializedName

data class SentencePatternListDto(
    @SerializedName("recent") val recent: List<SentencePatternDto>,
    @SerializedName("all") val all: List<SentencePatternDto>
)
