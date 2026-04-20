package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Word
import com.google.gson.annotations.SerializedName

data class WordDto(
    @SerializedName("id") val id: Long,
    @SerializedName("term") val term: String,
    @SerializedName("definition") val definition: String,
    @SerializedName("description") val description: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("word_set_id") val wordSetId: Long?,
    @SerializedName("flashcard_order") val flashcardOrder: Int? = null
)

fun WordDto.toDomain() = Word(
    id = id,
    term = term,
    definition = definition,
    description = description,
    status = status ?: "unknown",
    wordSetId = wordSetId,
    flashcardOrder = flashcardOrder ?: 0
)
