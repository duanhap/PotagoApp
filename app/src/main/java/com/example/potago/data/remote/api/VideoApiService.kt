package com.example.potago.data.remote.api

import com.example.potago.data.remote.dto.SubtitleDto
import com.example.potago.data.remote.dto.VideoDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
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

    @GET("/api/videos/recent")
    suspend fun getRecentVideos(
        @Query("page") page: Int?,
        @Query("size") size: Int?
    ): ApiResponse<List<VideoDto>>

    @GET("/api/videos/{video_id}")
    suspend fun getVideoById(
        @Path("video_id") videoId: Int
    ): ApiResponse<VideoDto>

    @DELETE("/api/videos/{video_id}")
    suspend fun deleteVideo(
        @Path("video_id") videoId: Int
    ): ApiResponse<Unit>

    @POST("/api/videos/my")
    suspend fun createMyVideo(
        @Body request: CreateVideoRequest
    ): ApiResponse<VideoDto>

    @POST("/api/videos/public/{public_video_id}/open")
    suspend fun openPublicVideo(
        @Path("public_video_id") publicVideoId: Int
    ): ApiResponse<VideoDto>

    @POST("/api/subtitles/{video_id}/sync-job")
    suspend fun syncJobStatus(
        @Path("video_id") videoId: Int,
        @Body request: SyncJobRequest
    ): ApiResponse<JobInfoResponse>

    @POST("/api/subtitles/{video_id}/cancel-job")
    suspend fun cancelJob(
        @Path("video_id") videoId: Int,
        @Body request: CancelJobRequest
    ): ApiResponse<Unit>

    @GET("/api/subtitles/{video_id}")
    suspend fun getSubtitles(
        @Path("video_id") videoId: Int
    ): ApiResponse<List<SubtitleDto>>
}

data class CreateVideoRequest(
    val title: String? = null,
    val thumbnail: String? = null,
    val source_url: String,
    val type_video: String,
    val definition_lang_code: String,
    val term_lang_code: String
)

data class SyncJobRequest(
    val job_id: String
)

data class CancelJobRequest(
    val job_id: String
)

data class JobInfoResponse(
    val status: String,
    val progress: Int? = null,
    val error: String? = null
)
