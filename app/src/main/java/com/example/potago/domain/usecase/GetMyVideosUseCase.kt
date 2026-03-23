package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.repository.VideoRepository
import javax.inject.Inject

class GetMyVideosUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(typeVideo: String?, page: Int?, size: Int?): Result<List<Video>> {
        return videoRepository.getMyVideos(typeVideo, page, size)
    }
}
