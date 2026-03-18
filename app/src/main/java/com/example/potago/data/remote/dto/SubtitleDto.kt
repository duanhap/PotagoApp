package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Subtitle
import com.google.gson.annotations.SerializedName

data class SubtitleDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("start_time") val startTime: Int?,
    @SerializedName("end_time") val endTime: Int?,
    @SerializedName("content") val content: String?,
    @SerializedName("pronunciation") val pronunciation: String?,
    @SerializedName("translation") val translation: String?,
    @SerializedName("video_id") val videoId: Int?
)

fun SubtitleDto.toDomain(): Subtitle {
    return Subtitle(
        id = id ?: 0,
        startTime = startTime,
        endTime = endTime,
        content = content,
        pronunciation = pronunciation,
        translation = translation,
        videoId = videoId ?: 0
    )
}
