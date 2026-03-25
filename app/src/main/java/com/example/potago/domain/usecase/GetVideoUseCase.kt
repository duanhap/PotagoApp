package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.repository.VideoRepository
import javax.inject.Inject

class GetVideoUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(videoId: Int): Result<Video> {
        return videoRepository.getVideo(videoId)
    }
}
