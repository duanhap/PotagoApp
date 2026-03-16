package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.VideoDto
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoApiService {
    @GET("/api/videos/public")
    suspend fun getPublicVideos(
        @Query("term_lang_code") termLangCode: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): ApiResponse<List<VideoDto>>

    @GET("/api/videos/my")
    suspend fun getMyVideos(
        @Query("type_video") typeVideo: String?,
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): ApiResponse<List<VideoDto>>
}
