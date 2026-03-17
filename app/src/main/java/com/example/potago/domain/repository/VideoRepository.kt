package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video

interface VideoRepository {
    suspend fun getPublicVideos(termLangCode: String?, page: Int?, size: Int?): Result<List<Video>>
    suspend fun getMyVideos(typeVideo: String?, page: Int?, size: Int?): Result<List<Video>>
    suspend fun getRecentVideos(page: Int?, size: Int?): Result<List<Video>>
    suspend fun deleteVideo(videoId: Int): Result<Unit>
    
    suspend fun createMyVideo(
        title: String?,
        thumbnail: String?,
        sourceUrl: String,
        typeVideo: String,
        definitionLangCode: String,
        termLangCode: String
    ): Result<Pair<Video, String?>>

    suspend fun syncJobStatus(videoId: Int, jobId: String): Result<JobStatus>
    suspend fun cancelJob(videoId: Int, jobId: String): Result<Unit>
}

data class JobStatus(
    val status: String,
    val progress: Int?,
    val error: String?
)
