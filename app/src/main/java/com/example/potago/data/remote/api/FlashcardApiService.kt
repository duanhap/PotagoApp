package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.WordDto
import com.example.potago.domain.model.Word
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query
import retrofit2.http.Body

interface FlashcardApiService {
    @GET("/api/flashcards")
    suspend fun getFlashcards(
        @Query("word_set_id") wordSetId: Long,
        @Query("mode") mode: String,
        @Query("current_word_id") currentWordId: Long? = null,
        @Query("size") size: Int = 20,
        @Query("filter") filter: String = "all"
    ): ApiResponse<List<WordDto>>

    @PUT("/api/words")
    suspend fun updateWord(
        @Query("word_id") wordId: Long,
        @Body body: Map<String, String?>
    ): ApiResponse<WordDto>
}
