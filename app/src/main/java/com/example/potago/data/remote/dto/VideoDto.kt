package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Video
import com.google.gson.annotations.SerializedName

data class VideoDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("title") val title: String?,
    @SerializedName("thumbnail") val thumbnail: String?,
    @SerializedName("source_url") val sourceUrl: String?,
    @SerializedName("last_opened") val lastOpened: String?,
    @SerializedName("type_video") val typeVideo: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("public_video_id") val publicVideoId: Int?,
    @SerializedName("definition_lang_code") val definitionLangCode: String?,
    @SerializedName("term_lang_code") val termLangCode: String?,
    @SerializedName("server_source_url") val serverSourceUrl: String?,
    @SerializedName("job_id") val jobId: String? = null ,// Thêm dòng này,
)

fun VideoDto.toDomain(): Video {
    return Video(
        id = id ?: 0,
        title = title,
        thumbnail = thumbnail,
        sourceUrl = sourceUrl ?: "",
        lastOpened = lastOpened,
        typeVideo = typeVideo,
        createdAt = createdAt,
        userId = userId,
        publicVideoId = publicVideoId,
        definitionLanguageCode = definitionLangCode ?: "",
        termLanguageCode = termLangCode ?: "",
        serverSourceUrl = serverSourceUrl?:""
    )
}
