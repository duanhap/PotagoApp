package com.example.potago.domain.usecase

import com.example.potago.domain.model.Result
import com.example.potago.domain.model.Video
import com.example.potago.domain.repository.VideoRepository
import javax.inject.Inject

class OpenPublicVideoUseCase @Inject constructor(
    private val videoRepository: VideoRepository
) {
    suspend operator fun invoke(publicVideoId: Int): Result<Video> {
        return videoRepository.openPublicVideo(publicVideoId)
    }
}
