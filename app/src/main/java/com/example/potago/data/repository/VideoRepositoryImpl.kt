package com.example.potago.data.repository

import com.example.potago.data.remote.api.VideoApiService
import com.example.potago.data.remote.dto.toDomain
import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.repository.VideoRepository
import javax.inject.Inject

class VideoRepositoryImpl @Inject constructor(
    private val videoApiService: VideoApiService
) : VideoRepository {
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
            Result.Error(e.message ?: "Unknown error")
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
            Result.Error(e.message ?: "Unknown error")
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
            Result.Error(e.message ?: "Unknown error")
        }
    }
}

