package com.example.potago.data.remote.dto

import com.example.potago.domain.model.Word
import com.google.gson.annotations.SerializedName

data class WordDto (
    @SerializedName("id") val id: Long?,
    @SerializedName("term") val term: String?,
    @SerializedName("definition") val definition: String?,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("word_set_id") val wordSetId: Long?,
    @SerializedName("flashcard_game_id") val flashcardGameId: Long?,
    @SerializedName("match_game_id") val matchGameId: Int?,
    @SerializedName("flashcard_order") val flashcardOrder: Int?
)

fun WordDto.toDomain(): Word {
    return Word(
        id = id ?: 0L,
        term = term.orEmpty(),
        definition = definition.orEmpty(),
        description = description,
        createdAt = createdAt.orEmpty(),
        status = status.orEmpty(),
        wordSetId = wordSetId,
        flashcardGameId = flashcardGameId,
        matchGameId = matchGameId,
        flashcardOrder = flashcardOrder ?: 0
    )
}
