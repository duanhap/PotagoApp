package com.example.potago.data.remote.dto

import com.example.potago.domain.model.WordSet
import com.google.gson.annotations.SerializedName

data class WordSetDto(
    @SerializedName("id") val id: Long?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("is_public") val isPublic: Boolean?,
    @SerializedName("def_lang_code") val defLangCode: String?,
    @SerializedName("term_lang_code") val termLangCode: String?,
    @SerializedName("updated_at") val updatedAt: String?,
    @SerializedName("last_opened") val lastOpened: String?,
    @SerializedName("user_id") val userId: Int?,
    @SerializedName("amount_of_words") val amountOfWords: Int? = null
)

fun WordSetDto.toDomain(): WordSet {
    return WordSet(
        id = id ?: 0L,
        name = name.orEmpty(),
        description = description,
        createdAt = createdAt.orEmpty(),
        isPublic = isPublic,
        definitionLanguageCode = defLangCode.orEmpty(),
        termLanguageCode = termLangCode.orEmpty(),
        updatedAt = updatedAt,
        lastOpened = lastOpened,
        userId = userId ?: 0,
        amountOfWords =  amountOfWords
    )
}

data class CreateWordSetRequest(
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("is_public") val isPublic: Boolean = false,
    @SerializedName("def_lang_code") val defLangCode: String,
    @SerializedName("term_lang_code") val termLangCode: String
)

data class WordInputDto(
    @SerializedName("term") val term: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("description") val description: String? = null
)

data class CreateWordSetWithWordsRequest(
    @SerializedName("word_set_id") val wordSetId: Long,
    @SerializedName("words") val words: List<WordInputDto>
)
