package com.example.potago.domain.repository

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video

interface VideoRepository {
    suspend fun getPublicVideos(termLangCode: String?, page: Int?, size: Int?): Result<List<Video>>
    suspend fun getMyVideos(typeVideo: String?, page: Int?, size: Int?): Result<List<Video>>
}
