package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Setence
import com.google.gson.annotations.SerializedName

data class SentenceDto(
    @SerializedName("id") val id: Int?,
    @SerializedName("term") val term: String?,
    @SerializedName("definition") val definition: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("number_of_mistakes") val mistakes: Int?,
    @SerializedName("sentence_pattern_id") val patternId: Int?,
    @SerializedName("last_opened") val lastOpened: String?,
    @SerializedName("term_language_code") val termLanguageCode: String?,
    @SerializedName("definition_language_code") val definitionLanguageCode: String?
)

fun SentenceDto.toDomain(): Setence {
    return Setence(
        id = id ?: 0,
        term = term.orEmpty(),
        definition = definition.orEmpty(),
        createdAt = createdAt.orEmpty(),
        status = status.orEmpty(),
        numberOfMistakes = mistakes,
        setencePatternId = patternId ?: 0,
        termLanguageCode = termLanguageCode.orEmpty(),
        definitionLanguageCode = definitionLanguageCode.orEmpty()
    )
}
