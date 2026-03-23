package com.example.potago.data.remote.dto

import com.example.potago.domain.model.SetencePattern
import com.google.gson.annotations.SerializedName

data class SentencePatternDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("is_public") val isPublic: Boolean?,
    @SerializedName("term_lang_code") val termLangCode: String?,
    @SerializedName("def_lang_code") val defLangCode: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("last_opened") val lastOpened: String?,
    @SerializedName("user_id") val userId: Int?
)

fun SentencePatternDto.toDomain(): SetencePattern {
    return SetencePattern(
        id = id ?: 0,
        name = name.orEmpty(),
        description = description.orEmpty(),
        createdAt = createdAt.orEmpty(),
        isPublic = isPublic,
        termLanguageCode = termLangCode.orEmpty(),
        definitionLanguageCode = defLangCode.orEmpty(),
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        userId = userId ?: 0
    )
}

