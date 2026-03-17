package com.example.potago.data.repository

import com.example.potago.data.remote.api.ApiResponse
import com.example.potago.data.remote.api.CancelJobRequest
import com.example.potago.data.remote.api.CreateVideoRequest
import com.example.potago.data.remote.api.SyncJobRequest
import com.example.potago.data.remote.api.VideoApiService
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.repository.JobStatus
import com.example.potago.domain.repository.VideoRepository
import com.google.gson.Gson
import retrofit2.HttpException
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoApiService: VideoApiService
) : VideoRepository {
    private val gson = Gson()

    override suspend fun getPublicVideos(
        termLangCode: String?,
        page: Int?,
        size: Int?
    ): Result<List<Video>> {
        return try {
            val response = videoApiService.getPublicVideos(termLangCode, page, size)

            if (response.success) {
                val videos = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(videos)
            } else {
                Result.Error(response.message)
            }

        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun getMyVideos(
        typeVideo: String?,
        page: Int?,
        size: Int?
    ): Result<List<Video>> {
        return try {
            val response = videoApiService.getMyVideos(typeVideo, page, size)
            if (response.success) {
                val videos = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(videos)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }
    override suspend fun getRecentVideos(page: Int?, size: Int?): Result<List<Video>> {
        return try {
            val response = videoApiService.getRecentVideos(page, size)
            if (response.success) {
                val videos = response.data?.map { it.toDomain() } ?: emptyList()
                Result.Success(videos)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun deleteVideo(videoId: Int): Result<Unit> {
        return try {
            val response = videoApiService.deleteVideo(videoId)
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun createMyVideo(
        title: String?,
        thumbnail: String?,
        sourceUrl: String,
        typeVideo: String,
        definitionLangCode: String,
        termLangCode: String
    ): Result<Pair<Video, String?>> {
        return try {
            val request = CreateVideoRequest(
                title = title,
                thumbnail = thumbnail,
                source_url = sourceUrl,
                type_video = typeVideo,
                definition_lang_code = definitionLangCode,
                term_lang_code = termLangCode
            )
            val response = videoApiService.createMyVideo(request)
            if (response.success && response.data != null) {
                val video = response.data.toDomain()
                Result.Success(Pair(video, response.data.jobId))
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun syncJobStatus(videoId: Int, jobId: String): Result<JobStatus> {
        return try {
            val request = SyncJobRequest(job_id = jobId)
            val response = videoApiService.syncJobStatus(videoId, request)
            if (response.success && response.data != null) {
                Result.Success(
                    JobStatus(
                        status = response.data.status,
                        progress = response.data.progress,
                        error = response.data.error
                    )
                )
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    override suspend fun cancelJob(videoId: Int, jobId: String): Result<Unit> {
        return try {
            val request = CancelJobRequest(job_id = jobId)
            val response = videoApiService.cancelJob(videoId, request)
            if (response.success) {
                Result.Success(Unit)
            } else {
                Result.Error(response.message)
            }
        } catch (e: Exception) {
            handleError(e)
        }
    }

    private fun <T> handleError(e: Exception): Result<T> {
        return if (e is HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                val apiResponse = gson.fromJson(errorBody, ApiResponse::class.java)
                Result.Error(apiResponse.message ?: "Lỗi hệ thống (${e.code()})")
            } catch (jsonEx: Exception) {
                Result.Error("Lỗi: ${e.message()}")
            }
        } else {
            Result.Error(e.message ?: "Lỗi không xác định")
        }
    }
}
